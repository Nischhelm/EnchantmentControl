package enchantmentcontrol.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
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
    public static final String ENCH_CFG_OUT_PATH = "config/enchantmentcontrol/enchantments_out.json";

    public static void preInit(){
        //create EnchantmentInfo from json cfg files, wish i had Meldexun Forge Config Extension

        try {
            List<EnchantmentInfo> infos = readWithGson(Files.newInputStream(new File(ENCH_CFG_PATH).toPath()));
        } catch (Exception e){
            EnchantmentControl.LOGGER.warn("Reading enchantment configs failed!");
            e.printStackTrace(System.out);
        }
    }

    public static void postInit(){
        if (!ConfigHandler.printDefaults) return;
        try {
            writeAllInfos();
        } catch (IOException e) {
            EnchantmentControl.LOGGER.warn("Writing enchantment defaults failed!");
            e.printStackTrace(System.out);
        }
    }

    private static List<EnchantmentInfo> readWithGson(InputStream in) throws IOException {
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(EnchantmentInfo.class, new EnchantmentInfoDeserializer())
                    .create();
            java.lang.reflect.Type listType = new TypeToken<List<EnchantmentInfo>>(){}.getType();
            List<EnchantmentInfo> infos = gson.fromJson(reader, listType);
            return infos == null ? new ArrayList<>() : infos;
        }
    }

    private static class EnchantmentInfoDeserializer implements JsonDeserializer<EnchantmentInfo> {
        @Override
        public EnchantmentInfo deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) return null;
            JsonObject o = json.getAsJsonObject();

            String id = getAsString(o, "id");
            if (id == null) return null;

            EnchantmentInfo info = new EnchantmentInfo(id);

            // rarity
            String rarityStr = getAsString(o, "rarity");
            if (rarityStr != null) info.setRarity(Enchantment.Rarity.valueOf(rarityStr));

            // booleans (with legacy aliases)
            Boolean isTreasure = getAsBoolean(o, "isTreasure");
            if (isTreasure == null) isTreasure = getAsBoolean(o, "treasure");
            if (isTreasure != null) info.setTreasure(isTreasure);

            Boolean isCurse = getAsBoolean(o, "isCurse");
            if (isCurse == null) isCurse = getAsBoolean(o, "curse");
            if (isCurse != null) info.setCurse(isCurse);

            Boolean allowedOnBooks = getAsBoolean(o, "isAllowedOnBooks");
            if (allowedOnBooks == null) allowedOnBooks = getAsBoolean(o, "allowed_on_books");
            if (allowedOnBooks != null) info.setAllowedOnBooks(allowedOnBooks);

            // levels (with legacy aliases)
            Integer minLvl = getAsInt(o, "minLvl");
            if (minLvl == null) minLvl = getAsInt(o, "min_lvl");
            if (minLvl != null) info.setMinLvl(minLvl);

            Integer maxLvl = getAsInt(o, "maxLvl");
            if (maxLvl == null) maxLvl = getAsInt(o, "max_lvl");
            if (maxLvl != null) info.setMaxLvl(maxLvl);

            // enchantability
            if (o.has("enchantability") && o.get("enchantability").isJsonObject()) {
                JsonObject enchObj = o.getAsJsonObject("enchantability");
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
            List<EntityEquipmentSlot> slots = readEnumList(o, "slots", EntityEquipmentSlot.class);
            if (slots == null) slots = readEnumList(o, "equipment_slots", EntityEquipmentSlot.class);
            if (slots != null) info.setSlots(slots);

            // types
            List<String> types = readStringList(o, "types");
            if (types == null && o.has("type") && o.get("type").isJsonPrimitive()) {
                types = Collections.singletonList(o.get("type").getAsString());
            }
            if (types != null) info.setEnchTableTypes(new HashSet<>(types));

            List<String> typesAnvil = readStringList(o, "typesAnvil");
            if (typesAnvil != null) info.setAnvilTypes(new HashSet<>(typesAnvil));

            // displayColor
            String colorStr = getAsString(o, "displayColor");
            if (colorStr != null) info.setTextDisplayColor(TextFormatting.valueOf(colorStr));

            // doublePrice (with legacy alias)
            Boolean doublePrice = getAsBoolean(o, "doublePrice");
            if (doublePrice == null) doublePrice = getAsBoolean(o, "double_price");
            if (doublePrice != null) info.setDoublePrice(doublePrice);

            // legacy enchantments control support
            Boolean legacyCustom = getAsBoolean(o, "custom_evaluations");
            String legacyMin = getAsString(o, "min_ench_eval");
            String legacyMax = getAsString(o, "max_ench_eval");
            if (Boolean.TRUE.equals(legacyCustom) && legacyMin != null && legacyMax != null) {
                parseLegacyEnchantmentsControlConfig(info, legacyMin, legacyMax);
            }

            return info;
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
        private static List<String> readStringList(JsonObject o, String key) {
            if (!o.has(key) || o.get(key).isJsonNull()) return null;
            JsonElement e = o.get(key);
            if (!e.isJsonArray()) return null;
            List<String> out = new ArrayList<>();
            for (JsonElement je : e.getAsJsonArray()) {
                if (je.isJsonPrimitive()) out.add(je.getAsString());
            }
            return out;
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
    }

    private static void writeAllInfos() throws IOException {
        // Ensure directory exists
        File outFile = new File(ENCH_CFG_OUT_PATH);
        File parent = outFile.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        JsonArray arr = new JsonArray();
        for (EnchantmentInfo info : EnchantmentInfo.getAll()) {
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

            // types
            if (info.typesEnchTable != null) {
                JsonArray types = new JsonArray();
                for (String t : info.typesEnchTable) types.add(new JsonPrimitive(t));
                o.add("types", types);
            }
            if (info.typesAnvil != null) {
                JsonArray typesAnvil = new JsonArray();
                for (String t : info.typesAnvil) typesAnvil.add(new JsonPrimitive(t));
                o.add("typesAnvil", typesAnvil);
            }

            // displayColor
            if (info.displayColor != null) o.addProperty("displayColor", info.displayColor.name());

            // doublePrice
            if (info.overwritesDoublePrice) o.addProperty("doublePrice", info.doublePrice);

            arr.add(o);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer w = new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8)) {
            gson.toJson(arr, w);
        }
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
