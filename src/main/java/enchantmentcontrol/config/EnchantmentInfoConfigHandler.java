package enchantmentcontrol.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.util.EnchantmentInfo;
import enchantmentcontrol.util.MaxEnchantabilityMode;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.text.TextFormatting;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EnchantmentInfoConfigHandler {
    public static final String ENCH_CFG_PATH = "config/enchantmentcontrol/enchantments.json";

    public static void preInit(){
        //create EnchantmentInfo from json cfg files, wish i had Meldexun Forge Config Extension

        try {
            List<EnchantmentInfo> infos = readJsonStream(Files.newInputStream(new File(ENCH_CFG_PATH).toPath()));
        } catch (Exception e){
            EnchantmentControl.LOGGER.warn("Reading enchantment configs failed!");
            e.printStackTrace(System.out);
        }
    }

    private static List<EnchantmentInfo> readJsonStream(InputStream in) throws IOException {
        try (JsonReader reader = new JsonReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return readEnchantmentInfosArray(reader);
        }
    }

    private static List<EnchantmentInfo> readEnchantmentInfosArray(JsonReader reader) throws IOException {
        List<EnchantmentInfo> EnchantmentInfos = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            EnchantmentInfos.add(readEnchantmentInfo(reader));
        }
        reader.endArray();
        return EnchantmentInfos;
    }

    private static EnchantmentInfo readEnchantmentInfo(JsonReader reader) throws IOException {
        String id = null;
        Enchantment.Rarity rarity = null;
        Boolean isTreasure = null;
        Boolean isCurse = null;
        Boolean isAllowedOnBooks = null;
        Integer minLvl = null;
        Integer maxLvl = null;

        Boolean custom_evaluations = null;
        String min_ench_eval = null;
        String max_ench_eval = null;

        Consumer<EnchantmentInfo> enchantabilitySetter = null;

        List<String> types = null;
        List<String> typesAnvil = null;
        List<EntityEquipmentSlot> slots = null;

        String displayColor = null;

        //TODO: a separate config file for just the descriptions

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name){
                case "id": id = reader.nextString(); break;
                case "rarity": rarity = Enchantment.Rarity.valueOf(reader.nextString()); break;
                case "isTreasure": case "treasure": isTreasure = reader.nextBoolean(); break;
                case "isCurse": case "curse": isCurse = reader.nextBoolean(); break;
                case "isAllowedOnBooks": case "allowed_on_books": isAllowedOnBooks = reader.nextBoolean(); break;
                case "minLvl": case "min_lvl": minLvl = reader.nextInt(); break;
                case "maxLvl": case "max_lvl": maxLvl = reader.nextInt(); break;
                case "enchantability": enchantabilitySetter = readEnchantability(reader); break;
                case "slots": case "equipment_slots": slots = readStringArray(reader).stream().map(EntityEquipmentSlot::valueOf).collect(Collectors.toList()); break;
                case "types": if(reader.peek() != JsonToken.NULL) types = readStringArray(reader); break;
                case "typesAnvil": if(reader.peek() != JsonToken.NULL) typesAnvil = readStringArray(reader); break;
                case "displayColor": displayColor = reader.nextString(); break;
                // --- only old enchcontrol ---
                case "type": types = Collections.singletonList(reader.nextString()); break;
                case "custom_evaluations": custom_evaluations = reader.nextBoolean(); break;
                case "min_ench_eval": min_ench_eval = reader.nextString(); break;
                case "max_ench_eval": max_ench_eval = reader.nextString(); break;
                /*
                Ignoring the following old Enchantments Control values
                    name <- unlocalised name, idk why this would ever need to be changed
                    enabled <- handled via registration blacklist
                    hide_on_item <- idk if i like it, might implement later
                    hide_on_book <- same
                    double_price <- might implement
                    incompat_mode <- idk why theres a mode. mode == 1 seems to fully disable all incompats
                    incompatible <- handled via config groups
                    applicability_mode <- i didnt understand this system
                    items_list_mode <- same
                    items_list <- same
                    description <- handled in a separate config TODO
                 */
                default: {
                    reader.skipValue();
                    //EnchantmentControl.LOGGER.warn("Skipping entry with name {}", name);
                    break;
                }
            }
        }
        reader.endObject();

        if(id == null) return null;

        EnchantmentInfo info = new EnchantmentInfo(id);
        if(rarity != null) info.setRarity(rarity);
        if(isTreasure != null) info.setTreasure(isTreasure);
        if(isCurse != null) info.setCurse(isCurse);
        if(isAllowedOnBooks != null) info.setAllowedOnBooks(isAllowedOnBooks);
        if(minLvl != null) info.setMinLvl(minLvl);
        if(maxLvl != null) info.setMaxLvl(maxLvl);
        if(enchantabilitySetter != null) enchantabilitySetter.accept(info);
        if(slots != null) info.setSlots(slots);
        if(types != null) info.setEnchTableTypes(new HashSet<>(types));
        if(typesAnvil != null) info.setAnvilTypes(new HashSet<>(typesAnvil));
        if(displayColor != null) info.setTextDisplayColor(TextFormatting.valueOf(displayColor));

        if(custom_evaluations != null && custom_evaluations && min_ench_eval != null && max_ench_eval != null)
            parseLegacyEnchantmentsControlConfig(info, min_ench_eval, max_ench_eval);

        return new EnchantmentInfo(id);
    }

    private static List<String> readStringArray(JsonReader reader) throws IOException {
        List<String> strings = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            strings.add(reader.nextString());
        }
        reader.endArray();
        return strings;
    }

    public static Consumer<EnchantmentInfo> readEnchantability(JsonReader reader) throws IOException {
        Integer minEnch = null;
        Integer lvlSpan = null;
        Integer range = null;
        MaxEnchantabilityMode maxEnchMode = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name){
                case "minEnch": minEnch = reader.nextInt(); break;
                case "lvlSpan": lvlSpan = reader.nextInt(); break;
                case "range": range = reader.nextInt(); break;
                case "maxEnchMode": maxEnchMode = MaxEnchantabilityMode.valueOf(reader.nextString()); break;
                default: {
                    reader.skipValue();
                    //EnchantmentControl.LOGGER.warn("Skipping entry with name {}", name);
                    break;
                }
            }
        }
        reader.endObject();

        if(minEnch != null && range != null) {
            int finalMinEnch = minEnch;
            int finalRange = range;
            int finalLvlSpan = lvlSpan == null ? 0 : lvlSpan;
            MaxEnchantabilityMode finalMaxEnchMode = maxEnchMode;
            return info -> info.setEnchantabilities(finalMinEnch, finalLvlSpan, finalRange, finalMaxEnchMode);
        }
        return null;
    }

    private static void parseLegacyEnchantmentsControlConfig(EnchantmentInfo info, String minEnchEval, String maxEnchEval) {
        /*     "min_ench_eval": "10+20*(LVL-1)",
         *     "max_ench_eval": "(1+10*LVL)+50",*/

        String[] split = minEnchEval.split("\\+|(\\*\\(LVL-1\\))"); //this only works for pattern minEnch+lvlSpan*(LVL-1), but im not gonna recreate the custom eval system he used
        int minEnch = Integer.parseInt(split[0]);
        int lvlSpan = Integer.parseInt(split[1]);

        int range = 0;
        MaxEnchantabilityMode mode = null;

        if(maxEnchEval.startsWith("(1+10*LVL)+")) {
            mode = MaxEnchantabilityMode.SUPER;
            range = Integer.parseInt(maxEnchEval.substring(11)); //remove "(1+10*LVL)+")
        } else if(maxEnchEval.startsWith("MIN+")){
            mode = MaxEnchantabilityMode.NORMAL;
            range = Integer.parseInt(maxEnchEval.substring(4)); //remove "MIN+"
        } else if(maxEnchEval.matches("\\d+")){
            mode = MaxEnchantabilityMode.CONST;
            range = Integer.parseInt(maxEnchEval); //just numbers
        }

        info.setEnchantabilities(minEnch, lvlSpan, range, mode);
    }
}
