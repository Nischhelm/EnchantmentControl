package enchantmentcontrol.config.enchantmentinfojsons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.compat.enchcontrol.LegacyJsonReader;
import enchantmentcontrol.util.EnchantmentInfo;
import enchantmentcontrol.util.IEnchantmentPropertySetter;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class EnchantmentInfoConfigReader {
    public static final String MAIN_DIR = "config/enchantmentcontrol/enchantments";

    public static void preInit(){
        // Legacy enchantments.json support (read once, then rename to enchantments.legacy.unused)
        if(LegacyJsonReader.onPreInit()) return;

        // Per-enchantment files: config/enchantmentcontrol/enchantments/modid/enchid.json
        try {
            List<EnchantmentInfo> readInfos = readPerFileConfigs();
            EnchantmentInfo.registerAll(readInfos);
        } catch (Exception e){
            EnchantmentControl.LOGGER.warn("Reading enchantment configs failed!");
            e.printStackTrace(System.out);
        }
    }

    //Done in post init to modify the final fields ench.rarity and ench.applicableEquipmentTypes
    public static void applyManualOverrides(){
        for(EnchantmentInfo info : EnchantmentInfo.getAll()){
            if(info.rarity == null && info.slots == null) continue;
            Enchantment ench = EnchantmentInfo.getEnchantmentObject(info);
            if(!(ench instanceof IEnchantmentPropertySetter)) return;
            IEnchantmentPropertySetter setter = (IEnchantmentPropertySetter) ench;
            if(info.rarity != null) setter.ec$setRarity(info.rarity);
            if(info.slots != null) setter.ec$setSlots(info.slots.toArray(new EntityEquipmentSlot[0]));
            if(info.type != null) ench.type = info.type;
        }
    }

    public static List<EnchantmentInfo> readListWithGson(InputStream in) throws IOException {
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(EnchantmentInfo.class, new EnchantmentInfoDeserialiser())
                    .create();
            java.lang.reflect.Type listType = new TypeToken<List<EnchantmentInfo>>(){}.getType();
            List<EnchantmentInfo> infos = gson.fromJson(reader, listType);
            return infos == null ? new ArrayList<>() : infos;
        }
    }

    private static EnchantmentInfo readSingleWithGson(InputStream in) throws IOException {
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(EnchantmentInfo.class, new EnchantmentInfoDeserialiser())
                    .create();
            return gson.fromJson(reader, EnchantmentInfo.class);
        }
    }

    private static List<EnchantmentInfo> readPerFileConfigs() {
        File base = new File(MAIN_DIR);
        if (!base.exists() && !base.mkdir()) return new ArrayList<>(); // nothing to read but create the folder

        List<EnchantmentInfo> infos = new ArrayList<>();
        readPerFileDirRecursive(base, infos);
        return infos;
    }

    private static List<EnchantmentInfo> readPerFileDirRecursive(File dir, List<EnchantmentInfo> infos)  {
        File[] children = dir.listFiles();
        if (children == null) return infos;
        for (File f : children) {
            if (f.isDirectory()) {
                infos.addAll(readPerFileDirRecursive(f, infos));
            } else if (f.isFile() && f.getName().endsWith(".json")) {
                try (InputStream in = Files.newInputStream(f.toPath())) {
                    EnchantmentInfo info = readSingleWithGson(in);
                    if (info == null)
                        EnchantmentControl.LOGGER.warn("Skipping invalid enchantment json: {}", f.getPath());
                    else infos.add(info);
                } catch (Exception ex) {
                    EnchantmentControl.LOGGER.warn("Failed reading enchantment json: {}", f.getPath());
                    ex.printStackTrace(System.out);
                }
            }
        }
        return infos;
    }
}
