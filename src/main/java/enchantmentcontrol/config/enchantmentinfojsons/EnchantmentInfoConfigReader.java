package enchantmentcontrol.config.enchantmentinfojsons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.util.EnchantmentInfo;
import enchantmentcontrol.util.IEnchantmentPropertySetter;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class EnchantmentInfoConfigReader {
    public static final String LEGACY_PATH = "config/enchantmentcontrol/enchantments.json";
    public static final String MAIN_DIR = "config/enchantmentcontrol/enchantments";

    public static void preInit(){
        // Legacy enchantments.json support (read once, then rename to enchantments.legacy.unused)
        List<EnchantmentInfo> readInfos = readLegacyConfigs();
        if(!readInfos.isEmpty()){
            EnchantmentInfo.registerAll(readInfos);
            ConfigHandler.debug.printLoaded = true; //tmp set config value to true to print the imported files
            EnchantmentInfoWriter.modifiablePath = "legacy_enchcontrol-copy_from_here";
            return; //if found, will not load contents of /enchantments/ (will only print to legacy-copy_from_here, next restart is gonna be fine again)
        }

        // Per-enchantment files: config/enchantmentcontrol/enchantments/modid/enchid.json
        try {
            readInfos = readPerFileConfigs();
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
            IEnchantmentPropertySetter setter = (IEnchantmentPropertySetter) EnchantmentInfo.getEnchantmentObject(info);
            if(setter == null) continue;
            setter.ec$setRarity(info.rarity);
            setter.ec$setSlots(info.slots.toArray(new EntityEquipmentSlot[0]));
        }

        EnchantmentInfo.register(new EnchantmentInfo("somanyenchantments:lessersharpness")).sharpnessBehavior = (lvl, type) -> type == EnumCreatureAttribute.valueOf("DRAGON") ? 1000F * lvl : 0;
    }

    private static List<EnchantmentInfo> readListWithGson(InputStream in) throws IOException {
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
        if (!base.exists() || !base.isDirectory()) return new ArrayList<>(); // nothing to read

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

    private static List<EnchantmentInfo> readLegacyConfigs() {
        EnchantmentControl.LOGGER.info("Reading legacy enchantments.json...");
        List<EnchantmentInfo> infos = new ArrayList<>();

        File legacyFile = new File(LEGACY_PATH);
        if (legacyFile.exists() && legacyFile.isFile()) {
            try (InputStream in = Files.newInputStream(legacyFile.toPath())) {
                EnchantmentControl.LOGGER.info("Legacy enchantments.json found, parsing...");
                infos.addAll(readListWithGson(in));
                EnchantmentControl.LOGGER.info("Legacy enchantments.json parsed successfully. Read {} enchantments.", infos.size());

                // After parsing successfully: rename to enchantments.legacy.unused
                File renamed = new File("config/enchantmentcontrol/enchantments.legacy.unused");
                try {
                    if (renamed.exists() && !renamed.delete()) EnchantmentControl.LOGGER.warn("Could not delete existing {} to rename legacy file.", renamed.getPath());
                    boolean ok = legacyFile.renameTo(renamed);
                    if (!ok) EnchantmentControl.LOGGER.warn("Renaming legacy enchantments.json to {} failed.", renamed.getPath());
                } catch (SecurityException se) {EnchantmentControl.LOGGER.warn("Renaming legacy enchantments.json failed due to security manager.");}

            } catch (Exception e){
                EnchantmentControl.LOGGER.warn("Reading legacy enchantments.json failed! Keeping file in place for next run.");
                e.printStackTrace(System.out);
            }
        }
        return infos;
    }
}
