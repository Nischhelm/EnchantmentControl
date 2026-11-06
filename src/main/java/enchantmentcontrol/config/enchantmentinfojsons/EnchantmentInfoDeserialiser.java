package enchantmentcontrol.config.enchantmentinfojsons;

import com.google.gson.*;
import enchantmentcontrol.Tags;
import enchantmentcontrol.util.EnchantmentInfo;
import enchantmentcontrol.util.MaxEnchantabilityMode;
import enchantmentcontrol.util.vanillabehavior.ArthropodBehavior;
import enchantmentcontrol.util.vanillabehavior.ProtectionBehavior;
import enchantmentcontrol.util.vanillabehavior.SharpnessBehavior;
import enchantmentcontrol.util.vanillabehavior.ThornsBehavior;
import enchantmentcontrol.util.vanillasystem.ISystemOverride;
import enchantmentcontrol.util.vanillasystem.VanillaSystem;
import enchantmentcontrol.util.vanillasystem.VanillaSystemOverride;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnchantmentInfoDeserialiser implements JsonDeserializer<EnchantmentInfo>, JsonSerializer<EnchantmentInfo> {
    @Override
    public EnchantmentInfo deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        //Read from JSON to EnchantmentInfo
        if (!json.isJsonObject()) return null;
        JsonObject jsonObj = json.getAsJsonObject();

        String id = getAsString(jsonObj, "id");
        if (id == null) return null;

        EnchantmentInfo info = new EnchantmentInfo(id);

        // rarity
        String rarityStr = getAsString(jsonObj, "rarity");
        if (rarityStr != null) info.setRarity(Enchantment.Rarity.valueOf(rarityStr));

        // booleans (with legacy aliases)
        Boolean isTreasure = getAsBoolean(jsonObj, "isTreasure");
        if (isTreasure == null) isTreasure = getAsBoolean(jsonObj, "treasure");
        if (isTreasure != null) info.setTreasure(isTreasure);

        Boolean isCurse = getAsBoolean(jsonObj, "isCurse");
        if (isCurse == null) isCurse = getAsBoolean(jsonObj, "curse");
        if (isCurse != null) info.setCurse(isCurse);

        Boolean allowedOnBooks = getAsBoolean(jsonObj, "isAllowedOnBooks");
        if (allowedOnBooks == null) allowedOnBooks = getAsBoolean(jsonObj, "allowed_on_books");
        if (allowedOnBooks != null) info.setAllowedOnBooks(allowedOnBooks);

        // levels (with legacy aliases)
        Integer minLvl = getAsInt(jsonObj, "minLvl");
        if (minLvl == null) minLvl = getAsInt(jsonObj, "min_lvl");
        if (minLvl != null) info.setMinLvl(minLvl);

        Integer maxLvl = getAsInt(jsonObj, "maxLvl");
        if (maxLvl == null) maxLvl = getAsInt(jsonObj, "max_lvl");
        if (maxLvl != null) info.setMaxLvl(maxLvl);

        // enchantability
        if (jsonObj.has("enchantability") && jsonObj.get("enchantability").isJsonObject()) {
            JsonObject enchObj = jsonObj.getAsJsonObject("enchantability");
            Integer minEnch = getAsInt(enchObj, "minEnch");
            Integer lvlSpan = getAsInt(enchObj, "lvlSpan");
            Integer range = getAsInt(enchObj, "range");
            String modeStr = getAsString(enchObj, "maxEnchMode");
            MaxEnchantabilityMode mode = modeStr == null ? null : MaxEnchantabilityMode.valueOf(modeStr);
            if (minEnch != null && range != null) {
                info.setEnchantabilities(minEnch, lvlSpan == null ? 0 : lvlSpan, range, mode);
            }
        }

        // slots (with legacy alias)
        List<EntityEquipmentSlot> slots = readEnumList(jsonObj, "slots", EntityEquipmentSlot.class);
        if (slots == null) slots = readEnumList(jsonObj, "equipment_slots", EntityEquipmentSlot.class);
        if (slots != null) info.setSlots(slots);

        // type (also used by enchcontrol, kinda unnecessary)
        String type = getAsString(jsonObj, "type");
        if (type != null) info.setType(type);

        // displayColor
        String colorStr = getAsString(jsonObj, "displayColor");
        if (colorStr != null) info.setTextDisplayColor(TextFormatting.valueOf(colorStr));

        // doublePrice (with legacy alias)
        Boolean doublePrice = getAsBoolean(jsonObj, "doublePrice");
        if (doublePrice == null) doublePrice = getAsBoolean(jsonObj, "double_price");
        if (doublePrice != null) info.setDoublePrice(doublePrice);

        // hide in tooltips (with legacy aliases)
        Boolean hideOnItem = getAsBoolean(jsonObj, "hideOnItem");
        if (hideOnItem == null) hideOnItem = getAsBoolean(jsonObj, "hide_on_item");
        if (hideOnItem != null) info.setHideOnItem(hideOnItem);

        Boolean hideOnBook = getAsBoolean(jsonObj, "hideOnBook");
        if (hideOnBook == null) hideOnBook = getAsBoolean(jsonObj, "hide_on_book");
        if (hideOnBook != null) info.setHideOnBook(hideOnBook);

        // legacy enchantments control support
        Boolean legacyCustom = getAsBoolean(jsonObj, "custom_evaluations");
        String legacyMin = getAsString(jsonObj, "min_ench_eval");
        String legacyMax = getAsString(jsonObj, "max_ench_eval");
        if (Boolean.TRUE.equals(legacyCustom) && legacyMin != null && legacyMax != null) {
            EnchantmentInfo.EnchantabilityCalc calc = parseLegacyEvaluations(id, legacyMin, legacyMax);
            if (calc != null) info.setEnchantabilities(calc);
        }

        // vanilla system overrides
        if (jsonObj.has("vanilla system overrides") && jsonObj.get("vanilla system overrides").isJsonObject()) {
            JsonObject vanillaSystems = jsonObj.getAsJsonObject("vanilla system overrides");
            for(VanillaSystem system : VanillaSystem.values()) {
                Float val = getAsFloat(vanillaSystems, system.toString());
                if (val != null) info.registerVanillaSystemOverride(system, val);
            }


            // sharpness behavior
            if (vanillaSystems.has("sharpness-like behavior")) {
                JsonObject obj = vanillaSystems.getAsJsonObject("sharpness-like behavior");
                Float startDmg = getAsFloat(obj, "startDmg");
                Float dmgPerLvl = getAsFloat(obj, "dmgPerLvl");
                List<String> attributes = new ArrayList<>();
                obj.getAsJsonArray("attributes").forEach(attr -> attributes.add(attr.getAsString()));
                if(startDmg != null && dmgPerLvl != null && !attributes.isEmpty()) {
                    info.sharpnessBehavior = new SharpnessBehavior(startDmg, dmgPerLvl, attributes.toArray(new String[0]));
                }
            }

            // protection behavior
            if (vanillaSystems.has("protection-like behavior")) {
                JsonObject obj = vanillaSystems.getAsJsonObject("protection-like behavior");
                Integer startProt = getAsInt(obj, "startProt");
                Integer protPerLvl = getAsInt(obj, "protPerLvl");
                List<String> sources = new ArrayList<>();
                obj.getAsJsonArray("sources").forEach(source -> sources.add(source.getAsString()));
                if(startProt != null && protPerLvl != null && !sources.isEmpty()) {
                    info.protectionBehavior = new ProtectionBehavior(startProt, protPerLvl, sources.toArray(new String[0]));
                }
            }

            // arthropod behavior
            if (vanillaSystems.has("bane-of-arthropods-like behavior")) {
                JsonObject obj = vanillaSystems.getAsJsonObject("bane-of-arthropods-like behavior");
                Integer startAmp = getAsInt(obj, "startAmp");
                Float ampPerLvl = getAsFloat(obj, "ampPerLvl");
                Integer startDur = getAsInt(obj, "startDur");
                Float durPerLvl = getAsFloat(obj, "durPerLvl");
                String potion = getAsString(obj, "potion");
                List<String> attributes = new ArrayList<>();
                obj.getAsJsonArray("attributes").forEach(attr -> attributes.add(attr.getAsString()));

                if(startAmp != null && ampPerLvl != null && startDur != null && durPerLvl != null && potion != null && !attributes.isEmpty()) {
                    info.arthropodBehavior = new ArthropodBehavior()
                            .setAmplifier(startAmp, ampPerLvl)
                            .setDuration(startDur, durPerLvl)
                            .setPotion(potion);
                    attributes.forEach(attr -> info.arthropodBehavior.addCreatureAttribute(attr));
                }
            }

            // thorns behavior
            if (vanillaSystems.has("thorns-like behavior")) {
                JsonObject obj = vanillaSystems.getAsJsonObject("thorns-like behavior");
                Float startChance = getAsFloat(obj, "startChance");
                Float chancePerLvl = getAsFloat(obj, "chancePerLvl");
                Float startDmgMin = getAsFloat(obj, "startDmgMin");
                Float startDmgMax = getAsFloat(obj, "startDmgMax");
                Float dmgPerLvl = getAsFloat(obj, "dmgPerLvl");
                Integer duraDmg = getAsInt(obj, "duraDmg");

                if(startChance != null && chancePerLvl != null && startDmgMin != null && startDmgMax != null && dmgPerLvl != null && duraDmg != null) {
                    info.thornsBehavior = new ThornsBehavior(info)
                            .setChances(startChance, chancePerLvl)
                            .setDamage(startDmgMin, startDmgMax, dmgPerLvl)
                            .setDurabilityDamage(duraDmg);
                }
            }
        }

        return info;
    }

    @Override
    public JsonElement serialize(EnchantmentInfo info, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
        //Write from EnchantmentInfo to JSON
        JsonObject o = new JsonObject();

        // id
        o.addProperty("id", EnchantmentInfo.getEnchantmentId(info));

        // rarity
        if (info.rarity != null) o.addProperty("rarity", info.rarity.name());

        // booleans with overwrite flags
        if (info.overwritesIsTreasure) o.addProperty("isTreasure", info.isTreasure);
        if (info.overwritesIsCurse) o.addProperty("isCurse", info.isCurse);
        if (info.overwritesIsAllowedOnBooks) o.addProperty("isAllowedOnBooks", info.isAllowedOnBooks);

        // levels
        if (info.overwritesMinLvl) o.addProperty("minLvl", info.minLvl);
        if (info.overwritesMaxLvl) o.addProperty("maxLvl", info.maxLvl);

        // enchantability
        if (info.ench != null) {
            JsonObject ench = new JsonObject();
            ench.addProperty("minEnch", info.ench.minEnchLvl);
            ench.addProperty("lvlSpan", info.ench.enchLvlSpan);
            ench.addProperty("range", info.ench.enchLvlRange);
            if (info.ench.enchMode != null)
                ench.addProperty("maxEnchMode", info.ench.enchMode.name());
            o.add("enchantability", ench);
        }

        // slots
        if (info.slots != null) {
            JsonArray slots = new JsonArray();
            for (EntityEquipmentSlot slot : info.slots) slots.add(new JsonPrimitive(slot.name()));
            o.add("slots", slots);
        }

        // type
        if (info.type != null) o.addProperty("type", info.type.name());

        // displayColor
        if (info.displayColor != null) o.addProperty("displayColor", info.displayColor.name());

        // doublePrice
        if (info.overwritesDoublePrice) o.addProperty("doublePrice", info.doublePrice);

        // hide in tooltips
        if (info.overwritesHideOnItem) o.addProperty("hideOnItem", info.hideOnItem);
        if (info.overwritesHideOnBook) o.addProperty("hideOnBook", info.hideOnBook);

        // vanilla system override
        JsonObject vanillaSystems = writeVanillaOverrideEntries(info);
        if (!vanillaSystems.entrySet().isEmpty()) o.add("vanilla system overrides", vanillaSystems);

        return o;
    }

    @Nonnull
    private static JsonObject writeVanillaOverrideEntries(EnchantmentInfo info) {
        JsonObject vanillaSystems = new JsonObject();
        for(Map.Entry<VanillaSystem, ISystemOverride> entry: info.vanillaSystemStrengths.entrySet()){
            if(!(entry.getValue() instanceof VanillaSystemOverride)) continue;
            vanillaSystems.addProperty(entry.getKey().toString(), ((VanillaSystemOverride)entry.getValue()).multiplier);
        }
        return writeVanillaBehaviors(vanillaSystems, info);
    }

    @Nonnull
    private static JsonObject writeVanillaBehaviors(JsonObject systems, EnchantmentInfo info) {
        // sharpness behavior
        if (info.sharpnessBehavior != null) {
            JsonObject obj = new JsonObject();
            obj.addProperty("startDmg", info.sharpnessBehavior.startDmg);
            obj.addProperty("dmgPerLvl", info.sharpnessBehavior.dmgPerLvl);
            JsonArray attrs = new JsonArray();
            info.sharpnessBehavior.attributes.forEach(attrs::add);
            obj.add("attributes", attrs);

            systems.add("sharpness-like behavior", obj);
        }

        // protection behavior
        if (info.protectionBehavior != null) {
            JsonObject obj = new JsonObject();
            obj.addProperty("startProt", info.protectionBehavior.startProt);
            obj.addProperty("protPerLvl", info.protectionBehavior.protPerLvl);
            JsonArray sources = new JsonArray();
            info.protectionBehavior.sources.forEach(sources::add);
            obj.add("sources", sources);

            systems.add("protection-like behavior", obj);
        }

        // arthropod behavior
        if (info.arthropodBehavior != null) {
            JsonObject obj = new JsonObject();
            obj.addProperty("startAmp", info.arthropodBehavior.startAmp);
            obj.addProperty("ampPerLvl", info.arthropodBehavior.ampPerLevel);
            obj.addProperty("startDur", info.arthropodBehavior.startDuration);
            obj.addProperty("durPerLvl", info.arthropodBehavior.durationPerLevel);
            obj.addProperty("potion", info.arthropodBehavior.potionId);
            JsonArray attrs = new JsonArray();
            info.arthropodBehavior.attributes.forEach(attrs::add);
            obj.add("attributes", attrs);

            systems.add("bane-of-arthropods-like behavior", obj);
        }

        // thorns behavior
        if (info.thornsBehavior != null) {
            JsonObject obj = new JsonObject();
            obj.addProperty("startChance", info.thornsBehavior.startChance);
            obj.addProperty("chancePerLvl", info.thornsBehavior.chancePerLevel);
            obj.addProperty("startDmgMin", info.thornsBehavior.startDmgMin);
            obj.addProperty("startDmgMax", info.thornsBehavior.startDmgMax);
            obj.addProperty("dmgPerLvl", info.thornsBehavior.dmgPerLevel);
            obj.addProperty("duraDmg", info.thornsBehavior.durabilityDmg);

            systems.add("thorns-like behavior", obj);
        }
        return systems;
    }

    private static String getAsString(JsonObject o, String key) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsString() : null;
    }

    private static Boolean getAsBoolean(JsonObject o, String key) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsBoolean() : null;
    }

    private static Integer getAsInt(JsonObject o, String key) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsInt() : null;
    }

    private static Float getAsFloat(JsonObject o, String key) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsFloat() : null;
    }

    private static <E extends Enum<E>> List<E> readEnumList(JsonObject o, String key, Class<E> enumCls) {
        if (!o.has(key) || o.get(key).isJsonNull()) return null;
        JsonElement e = o.get(key);
        if (!e.isJsonArray()) return null;
        List<E> out = new ArrayList<>();
        for (JsonElement je : e.getAsJsonArray()) {
            if (je.isJsonPrimitive()) {
                out.add(Enum.valueOf(enumCls, je.getAsString()));
            }
        }
        return out;
    }

    // Enchantments Control wrote arbitrary formulas, we only recognise the patterns it generated by default (whitespace ignored):
    //   min_ench_eval: "A", "A+B*(LVL-1)", "A+(LVL-1)*B", "A+B*LVL", "B*LVL", "LVL*B"
    //   max_ench_eval: "(1+10*LVL)+R" / "1+10*LVL+R" (SUPER), "MIN+R" / "MIN" (NORMAL), "R" (CONST)
    private static final Pattern LEGACY_MIN_SPAN_LVL_MINUS_ONE = Pattern.compile("(-?\\d+)(?:\\+(-?\\d+)\\*\\(LVL-1\\)|\\+\\(LVL-1\\)\\*(-?\\d+))?");
    private static final Pattern LEGACY_MIN_SPAN_LVL = Pattern.compile("(?:(-?\\d+)\\+)?(?:(-?\\d+)\\*LVL|LVL\\*(-?\\d+))");
    private static final Pattern LEGACY_MAX_SUPER = Pattern.compile("\\(?1\\+10\\*LVL\\)?(?:\\+(-?\\d+))?");
    private static final Pattern LEGACY_MAX_NORMAL = Pattern.compile("MIN(?:\\+(-?\\d+))?");
    private static final Pattern LEGACY_MAX_CONST = Pattern.compile("-?\\d+");

    /** Translates Enchantments Control min/max enchantability formulas, returns null (and warns) if they can't be translated */
    @Nullable
    public static EnchantmentInfo.EnchantabilityCalc parseLegacyEvaluations(String id, String minEnchEval, String maxEnchEval) {
        String min = minEnchEval.replaceAll("\\s", "").toUpperCase(Locale.ROOT);
        String max = maxEnchEval.replaceAll("\\s", "").toUpperCase(Locale.ROOT);

        int minEnch, lvlSpan;
        Matcher m;
        if ((m = LEGACY_MIN_SPAN_LVL_MINUS_ONE.matcher(min)).matches()) {
            minEnch = Integer.parseInt(m.group(1));
            String span = m.group(2) != null ? m.group(2) : m.group(3);
            lvlSpan = span == null ? 0 : Integer.parseInt(span);
        } else if ((m = LEGACY_MIN_SPAN_LVL.matcher(min)).matches()) {
            lvlSpan = Integer.parseInt(m.group(2) != null ? m.group(2) : m.group(3));
            minEnch = (m.group(1) == null ? 0 : Integer.parseInt(m.group(1))) + lvlSpan; // A+B*LVL == (A+B)+B*(LVL-1)
        } else {
            Tags.LOGGER.warn("Legacy import of {}: unsupported min_ench_eval \"{}\", keeping default enchantabilities", id, minEnchEval);
            return null;
        }

        int range;
        MaxEnchantabilityMode mode;
        if ((m = LEGACY_MAX_SUPER.matcher(max)).matches()) {
            mode = MaxEnchantabilityMode.SUPER;
            range = m.group(1) == null ? 0 : Integer.parseInt(m.group(1));
        } else if ((m = LEGACY_MAX_NORMAL.matcher(max)).matches()) {
            mode = MaxEnchantabilityMode.NORMAL;
            range = m.group(1) == null ? 0 : Integer.parseInt(m.group(1));
        } else if (LEGACY_MAX_CONST.matcher(max).matches()) {
            mode = MaxEnchantabilityMode.CONST;
            range = Integer.parseInt(max);
        } else {
            Tags.LOGGER.warn("Legacy import of {}: unsupported max_ench_eval \"{}\", keeping default enchantabilities", id, maxEnchEval);
            return null;
        }

        return new EnchantmentInfo.EnchantabilityCalc(minEnch, lvlSpan, range, mode);
    }
}
