package enchantmentcontrol.compat.enchcontrol;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.Tags;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.descriptions.DescriptionReader;
import enchantmentcontrol.config.descriptions.EmptyEnchantmentWriter;
import enchantmentcontrol.config.enchantmentinfojsons.EnchantmentInfoConfigReader;
import enchantmentcontrol.config.enchantmentinfojsons.EnchantmentInfoDeserialiser;
import enchantmentcontrol.config.enchantmentinfojsons.EnchantmentInfoWriter;
import enchantmentcontrol.config.provider.IncompatibleConfigProvider;
import enchantmentcontrol.util.EnchantmentInfo;
import enchantmentcontrol.util.IOriginalEnchantmentProperties;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * One-time import of Enchantments Control (modid enchcontrol, by AustereTony) enchantments.json into our per-enchant jsons.
 * <p>
 * Enchantments Control requires every field for every entry, and on first launch copies its bundled defaults (all vanilla enchants) into the file.
 * So instead of copying entries 1:1 we diff every field against the actual (unmodified) enchantment and only write what was actually changed.
 * <p>
 * Afterwards the original file is backed up as enchantments.json.imported (.imported2, .imported3, ... if that already exists) and the enchcontrol file is replaced with "[]".
 * An empty array (instead of deleting the file) is what Enchantments Control itself uses for "no overrides" (/enchcontrol clear):
 * deleting it would make Enchantments Control fail to load its config.json too (zeroing its level caps), and it only regenerates the file if config.json is missing.
 * <p>
 * Needs registered enchantments, so it runs in init.
 * Rarity, type and slots are diffed against the values the enchantment was constructed/registered with ({@link IOriginalEnchantmentProperties}),
 * since Enchantments Control writes its values directly into those fields when it initialises its wrappers, which already happens before init.
 */
@Mod.EventBusSubscriber
public class EnchantmentsControlImporter {
    public static final String ENCHCONTROL_PATH = "config/enchcontrol/enchantments.json";

    /** Imported incompatibilities (enchant -> its "incompatible" list), merged into the incompatibility config by {@link IncompatibleConfigProvider} */
    public static final Map<Enchantment, Set<Enchantment>> importedIncompats = new HashMap<>();
    /** Imported descriptions (enchant id -> description with literal \n line breaks), written into descriptions.txt */
    private static final Map<String, String> importedDescriptions = new LinkedHashMap<>();
    /** Enchants with incompat_mode 1, where only the imported list should decide, not their own isCompatibleWith */
    public static final Set<Enchantment> replacesOwnIncompats = new HashSet<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEnchantmentRegistry(RegistryEvent.Register<Enchantment> event) {
        for (Enchantment ench : event.getRegistry()) ((IOriginalEnchantmentProperties) ench).ec$captureOriginalType();
    }

    public static void init() {
        if (!ConfigHandler.dev.importEnchantmentsControl) return;
        importFile(new File(ENCHCONTROL_PATH));
    }

    private static void importFile(File file) {
        if (!file.isFile()) return;

        JsonArray entries;
        try (Reader reader = new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8)) {
            JsonElement root = new JsonParser().parse(reader);
            if (root == null || !root.isJsonArray()) {
                Tags.LOGGER.warn("Enchantments Control import: {} is not a json array, skipping", file.getPath());
                return;
            }
            entries = root.getAsJsonArray();
        } catch (Exception e) {
            Tags.LOGGER.warn("Enchantments Control import: reading {} failed, skipping", file.getPath(), e);
            return;
        }
        if (entries.size() == 0) return; // already imported (or cleared via /enchcontrol clear)

        Tags.LOGGER.info("Enchantments Control import: importing {} entries from {}", entries.size(), file.getPath());
        File outDir = new File(EnchantmentInfoConfigReader.MAIN_DIR);
        int written = 0;
        for (JsonElement element : entries) {
            if (!element.isJsonObject()) continue;
            try {
                if (importEntry(element.getAsJsonObject(), outDir)) written++;
            } catch (Exception e) {
                Tags.LOGGER.warn("Enchantments Control import: failed to import entry {}", element, e);
            }
        }
        Tags.LOGGER.info("Enchantments Control import: wrote {} changed enchantments to {}", written, outDir.getPath());
        applyIncompats();
        applyDescriptions();

        // Back up the original, then neutralise it so it's not imported again and Enchantments Control doesn't apply it alongside our jsons
        try {
            File backup = new File(file.getPath() + ".imported");
            for (int i = 2; backup.exists(); i++) backup = new File(file.getPath() + ".imported" + i); // never overwrite an earlier backup
            Files.copy(file.toPath(), backup.toPath());
            Files.write(file.toPath(), "[]".getBytes(StandardCharsets.UTF_8));
            Tags.LOGGER.info("Enchantments Control import: original backed up to {}", backup.getPath());
        } catch (Exception e) {
            Tags.LOGGER.warn("Enchantments Control import: could not back up / clear {}. It will be imported again next launch (existing files are kept).", file.getPath(), e);
        }
    }

    /** @return whether a file was written */
    private static boolean importEntry(JsonObject o, File outDir) {
        String id = str(o, "id");
        if (id == null) return false;

        Enchantment ench = Enchantment.getEnchantmentByLocation(id);
        if (ench == null) {
            Tags.LOGGER.info("Enchantments Control import: {} is not registered, skipping", id);
            return false;
        }
        logUnsupported(id, o);
        collectIncompats(id, ench, o);
        collectDescription(id, o);
        if (Boolean.FALSE.equals(bool(o, "enabled"))) unregister(id);

        if (EnchantmentInfo.get(id) != null) {
            Tags.LOGGER.warn("Enchantments Control import: {} already has a config json, skipping", id);
            return false;
        }
        ResourceLocation loc = new ResourceLocation(id);
        File outFile = new File(new File(outDir, loc.getNamespace()), loc.getPath() + ".json");
        if (outFile.exists()) {
            Tags.LOGGER.warn("Enchantments Control import: {} already exists (but didn't load?), skipping", outFile.getPath());
            return false;
        }

        EnchantmentInfo info = new EnchantmentInfo(id);
        boolean changed = false;

        String rarity = str(o, "rarity");
        IOriginalEnchantmentProperties original = (IOriginalEnchantmentProperties) ench;
        if (rarity != null && !rarity.equals(original.ec$getOriginalRarity().name())) {
            try {
                info.setRarity(Enchantment.Rarity.valueOf(rarity));
                changed = true;
            } catch (IllegalArgumentException e) {
                Tags.LOGGER.warn("Enchantments Control import: {} has unknown rarity {}, ignoring", id, rarity);
            }
        }

        Boolean treasure = bool(o, "treasure");
        boolean treasureChanged = treasure != null && treasure != ench.isTreasureEnchantment();
        if (treasureChanged) { info.setTreasure(treasure); changed = true; }

        // Enchantments Control defaults double_price to the original treasure value.
        // If treasure gets changed, always write it, since we'd otherwise derive it from the new treasure value.
        Boolean doublePrice = bool(o, "double_price");
        if (doublePrice != null && (treasureChanged || doublePrice != ench.isTreasureEnchantment())) { info.setDoublePrice(doublePrice); changed = true; }

        Boolean curse = bool(o, "curse");
        if (curse != null && curse != ench.isCurse()) { info.setCurse(curse); changed = true; }

        Boolean books = bool(o, "allowed_on_books");
        if (books != null && books != ench.isAllowedOnBooks()) { info.setAllowedOnBooks(books); changed = true; }

        Integer minLvl = integer(o, "min_lvl");
        if (minLvl != null && minLvl != ench.getMinLevel()) { info.setMinLvl(minLvl); changed = true; }

        Integer maxLvl = integer(o, "max_lvl");
        if (maxLvl != null && maxLvl != ench.getMaxLevel()) { info.setMaxLvl(maxLvl); changed = true; }

        String type = str(o, "type");
        if (type != null && !type.equals("NULL") && (original.ec$getOriginalType() == null || !type.equals(original.ec$getOriginalType().name()))) {
            info.setType(type);
            if (info.type != null) changed = true;
        }

        List<EntityEquipmentSlot> slots = slots(id, o);
        if (slots != null && !new HashSet<>(slots).equals(new HashSet<>(Arrays.asList(original.ec$getOriginalSlots())))) {
            info.setSlots(slots);
            changed = true;
        }

        // Enchantments Control ignores the formulas unless custom_evaluations is true
        if (Boolean.TRUE.equals(bool(o, "custom_evaluations"))) {
            String minEval = str(o, "min_ench_eval");
            String maxEval = str(o, "max_ench_eval");
            EnchantmentInfo.EnchantabilityCalc calc = minEval == null || maxEval == null ? null : EnchantmentInfoDeserialiser.parseLegacyEvaluations(id, minEval, maxEval);
            if (calc != null && differsFromEnchant(calc, ench, minLvl != null ? minLvl : ench.getMinLevel(), maxLvl != null ? maxLvl : ench.getMaxLevel())) {
                info.setEnchantabilities(calc);
                changed = true;
            }
        }

        if (!changed) return false;

        EnchantmentInfo.register(info, ench); // apply this run already (rarity/slots/type get applied right after in postInit)
        EnchantmentInfoWriter.writeSingleEnchantmentInfo(info, outDir);
        return true;
    }

    private static boolean differsFromEnchant(EnchantmentInfo.EnchantabilityCalc calc, Enchantment ench, int minLvl, int maxLvl) {
        for (int lvl = minLvl; lvl <= maxLvl; lvl++)
            if (calc.getMinEnch(lvl) != ench.getMinEnchantability(lvl) || calc.getMaxEnch(lvl) != ench.getMaxEnchantability(lvl))
                return true;
        return false;
    }

    @Nullable
    private static List<EntityEquipmentSlot> slots(String id, JsonObject o) {
        if (!o.has("equipment_slots") || !o.get("equipment_slots").isJsonArray()) return null;
        List<EntityEquipmentSlot> out = new ArrayList<>();
        for (JsonElement e : o.getAsJsonArray("equipment_slots")) {
            String s = e.getAsString();
            if (s.equals("ALL")) return Arrays.asList(EntityEquipmentSlot.values());
            try {
                out.add(EntityEquipmentSlot.valueOf(s));
            } catch (IllegalArgumentException ex) {
                Tags.LOGGER.warn("Enchantments Control import: {} has unknown slot {}, ignoring slots", id, s);
                return null;
            }
        }
        // Enchantments Control treats an empty list as all slots
        return out.isEmpty() ? Arrays.asList(EntityEquipmentSlot.values()) : out;
    }

    /**
     * Enchantments Control's enabled=false only keeps the enchant out of the enchanting table, librarians and random loot, but it stays registered.
     * We unregister it instead. The registry blacklist is checked during registration, so this applies from the next startup on
     * (this startup Enchantments Control still has the original file loaded and keeps it out of those sources itself).
     */
    private static void unregister(String id) {
        List<String> blacklist = ConfigHandler.blacklists.blacklistedRegistryEnchants;
        if (blacklist.contains(id)) return;
        blacklist.add(id);
        EnchantmentControl.configNeedsSaving = true;
        Tags.LOGGER.info("Enchantments Control import: {} is disabled, added to \"Registered Enchantment Blacklist\". It will be unregistered from the next startup on.", id);
    }

    private static void collectIncompats(String id, Enchantment ench, JsonObject o) {
        if (nonZero(o, "incompat_mode")) replacesOwnIncompats.add(ench);
        if (!nonEmpty(o, "incompatible")) return;
        Set<Enchantment> listed = new HashSet<>();
        for (JsonElement e : o.getAsJsonArray("incompatible")) {
            Enchantment other = Enchantment.getEnchantmentByLocation(e.getAsString());
            if (other == null) Tags.LOGGER.info("Enchantments Control import: {} lists {} as incompatible, which is not registered, ignoring", id, e.getAsString());
            else if (other != ench) listed.add(other);
        }
        if (!listed.isEmpty()) importedIncompats.put(ench, listed);
    }

    /** Enchantments Control ran every description line through I18n, so lang keys get translated here (to the current language) */
    private static void collectDescription(String id, JsonObject o) {
        if (!nonEmpty(o, "description")) return;
        List<String> lines = new ArrayList<>();
        for (JsonElement e : o.getAsJsonArray("description")) {
            String line = e.getAsString();
            lines.add(I18n.canTranslate(line) ? I18n.translateToLocal(line) : line);
        }
        importedDescriptions.put(id, String.join("\\n", lines));
    }

    /** Fills the imported descriptions into descriptions.txt, only for enchants that don't have a description there yet */
    private static void applyDescriptions() {
        if (importedDescriptions.isEmpty()) return;
        Path path = Paths.get(DescriptionReader.PATH);
        try {
            EmptyEnchantmentWriter.write(DescriptionReader.PATH); // only writes if missing, so the file has a line for each enchant
            List<String> lines = new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));
            int added = 0;
            for (Map.Entry<String, String> entry : importedDescriptions.entrySet()) {
                String prefix = entry.getKey() + "=";
                int index = -1;
                for (int i = 0; i < lines.size(); i++) if (lines.get(i).trim().startsWith(prefix)) { index = i; break; }
                if (index >= 0 && !lines.get(index).trim().substring(prefix.length()).trim().isEmpty()) {
                    Tags.LOGGER.warn("Enchantments Control import: {} already has a description in {}, skipping", entry.getKey(), DescriptionReader.PATH);
                    continue;
                }
                String line = prefix + entry.getValue();
                if (index >= 0) lines.set(index, line);
                else lines.add(line);
                added++;
            }
            Files.write(path, lines, StandardCharsets.UTF_8);
            Tags.LOGGER.info("Enchantments Control import: added {} descriptions to {}", added, DescriptionReader.PATH);
        } catch (Exception e) {
            Tags.LOGGER.warn("Enchantments Control import: writing descriptions to {} failed", DescriptionReader.PATH, e);
        }
    }

    /**
     * On first setup ("Print Default Incompatibilities" enabled), the imported incompats get merged into the inferred incompatibility matrix in postInit.
     * Otherwise the groups are already set up, so each imported pair gets added as its own group.
     */
    private static void applyIncompats() {
        if (importedIncompats.isEmpty() && replacesOwnIncompats.isEmpty()) return;
        if (ConfigHandler.dev.printIncompats) {
            Tags.LOGGER.info("Enchantments Control import: incompatibilities will be merged into the inferred \"Incompatible Groups\"");
            return;
        }

        Map<String, ArrayList<String>> groups = ConfigHandler.incompatible.incompatibleGroups;
        int added = 0;
        for (Map.Entry<Enchantment, Set<Enchantment>> entry : importedIncompats.entrySet()) {
            for (Enchantment other : entry.getValue()) {
                if (!IncompatibleConfigProvider.areCompatibleByGroups(groups, entry.getKey(), other)) continue; // already in a shared group
                ArrayList<String> pair = new ArrayList<>(Arrays.asList(String.valueOf(entry.getKey().getRegistryName()), String.valueOf(other.getRegistryName())));
                Collections.sort(pair);
                groups.put("Enchantments Control: " + pair.get(0) + " + " + pair.get(1), pair);
                added++;
            }
        }
        if (added > 0) {
            Tags.LOGGER.info("Enchantments Control import: added {} incompatible pairs to \"Incompatible Groups\"", added);
            EnchantmentControl.configNeedsSaving = true;
        }
        for (Enchantment ench : replacesOwnIncompats)
            Tags.LOGGER.warn("Enchantments Control import: {} has incompat_mode 1 (ignore its own incompatibilities), which can't be applied to already set up \"Incompatible Groups\". Remove it from those groups manually if needed.", ench.getRegistryName());
    }

    /** Fields this mod has no per-enchant json equivalent for. Only logs them if they deviate from Enchantments Control's defaults. */
    private static void logUnsupported(String id, JsonObject o) {
        List<String> dropped = new ArrayList<>();
        if (Boolean.TRUE.equals(bool(o, "hide_on_item"))) dropped.add("hide_on_item");
        if (Boolean.TRUE.equals(bool(o, "hide_on_book"))) dropped.add("hide_on_book");
        if (nonZero(o, "applicability_mode") || nonZero(o, "items_list_mode") || nonEmpty(o, "items_list")) dropped.add("item applicability (use the \"Item Types\" configs)");
        if (!dropped.isEmpty())
            Tags.LOGGER.warn("Enchantments Control import: {} has settings that can't be imported automatically: {}", id, String.join(", ", dropped));
    }

    private static boolean nonZero(JsonObject o, String key) {
        Integer i = integer(o, key);
        return i != null && i != 0;
    }

    private static boolean nonEmpty(JsonObject o, String key) {
        return o.has(key) && o.get(key).isJsonArray() && o.getAsJsonArray(key).size() > 0;
    }

    @Nullable private static String str(JsonObject o, String key) {
        return o.has(key) && o.get(key).isJsonPrimitive() ? o.get(key).getAsString() : null;
    }

    @Nullable private static Boolean bool(JsonObject o, String key) {
        return o.has(key) && o.get(key).isJsonPrimitive() ? o.get(key).getAsBoolean() : null;
    }

    @Nullable private static Integer integer(JsonObject o, String key) {
        return o.has(key) && o.get(key).isJsonPrimitive() ? o.get(key).getAsInt() : null;
    }
}
