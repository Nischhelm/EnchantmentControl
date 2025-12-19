package enchantmentcontrol.config.enchantmentinfojsons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.util.EnchantmentInfo;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;

public class EnchantmentInfoWriter {
    public static String modifiablePath = "loaded";
    public static final String MAIN_DIR = "config/enchantmentcontrol/";

    public static void postInit(){
        if (!ConfigHandler.debug.printLoaded) return;
        writeAllCurrentEnchantmentInfos(EnchantmentInfo.getAll(), MAIN_DIR + modifiablePath);
    }

    public static void writeAllCurrentEnchantmentInfos(Collection<EnchantmentInfo> infos, String path) {
        // Write one file per enchantment into config/enchantmentcontrol/out/modid/enchid.json
        try {
            File baseOut = new File(path);
            if (!baseOut.exists() && !baseOut.mkdirs()) {
                EnchantmentControl.LOGGER.warn("Could not create directory: {}", baseOut.getPath());
            }

            // Clear any existing files so the directory reflects only the current run
            clearDirectoryContents(baseOut);

            for (EnchantmentInfo info : infos) {
                String id = EnchantmentInfo.getEnchantmentId(info);
                String[] split = id.split(":");
                String modid = split[0];
                String enchid = split[1];

                File modDir = new File(baseOut, modid);
                if (!modDir.exists() && !modDir.mkdirs()) {
                    EnchantmentControl.LOGGER.warn("Could not create directory: {}", modDir.getPath());
                }

                File outFile = new File(modDir, enchid + ".json");

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(EnchantmentInfo.class, new EnchantmentInfoDeserialiser())
                        .setPrettyPrinting()
                        .create();
                try (Writer w = new OutputStreamWriter(Files.newOutputStream(outFile.toPath()), StandardCharsets.UTF_8)) {
                    gson.toJson(info, EnchantmentInfo.class, w);
                }
            }
        } catch (IOException e) {
            EnchantmentControl.LOGGER.warn("Writing loaded enchantment infos failed!");
        }
    }

    public static void clearDirectoryContents(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return;
        File[] children = dir.listFiles();
        if (children == null) return;
        for (File f : children) {
            if (f.isDirectory()) {
                clearDirectoryContents(f);
            }
            if (!f.delete()) {
                EnchantmentControl.LOGGER.debug("Could not delete {} while clearing {}", f.getPath(), dir.getPath());
            }
        }
    }
}
