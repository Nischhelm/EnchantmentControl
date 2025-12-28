package enchantmentcontrol.compat.enchcontrol;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.enchantmentinfojsons.EnchantmentInfoConfigReader;
import enchantmentcontrol.config.enchantmentinfojsons.EnchantmentInfoWriter;
import enchantmentcontrol.util.EnchantmentInfo;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class LegacyJsonReader {
    public static final String LEGACY_PATH = "config/enchantmentcontrol/enchantments.json";

    public static boolean onPreInit(){
        // Legacy enchantments.json support (read once, then rename to enchantments.legacy.unused)
        List<EnchantmentInfo> readInfos = LegacyJsonReader.readLegacyConfigs();
        if(!readInfos.isEmpty()){
            EnchantmentInfo.registerAll(readInfos);
            ConfigHandler.debug.printLoaded = true; //tmp set config value to true to print the imported files
            EnchantmentInfoWriter.modifiablePath = "legacy_enchcontrol-copy_from_here";
            return true; //if found, will not load contents of /enchantments/ (will only print to legacy-copy_from_here, next restart is gonna be fine again)
        }
        return false;
    }

    public static List<EnchantmentInfo> readLegacyConfigs() {
        List<EnchantmentInfo> infos = new ArrayList<>();

        File legacyFile = new File(LEGACY_PATH);
        if (legacyFile.exists() && legacyFile.isFile()) {
            EnchantmentControl.LOGGER.info("Reading legacy enchantments.json...");
            try (InputStream in = Files.newInputStream(legacyFile.toPath())) {
                EnchantmentControl.LOGGER.info("Legacy enchantments.json found, parsing...");
                infos.addAll(EnchantmentInfoConfigReader.readListWithGson(in));
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
