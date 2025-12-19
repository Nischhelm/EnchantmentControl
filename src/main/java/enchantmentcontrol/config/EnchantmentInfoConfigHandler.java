package enchantmentcontrol.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.util.EnchantmentInfo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class EnchantmentInfoConfigHandler {
    public static final String ENCH_CFG_PATH = "config/enchantmentcontrol/enchantments.json";
    public static final String ENCH_CFG_PER_FILE_DIR = "config/enchantmentcontrol/enchantments";
    public static final String ENCH_CFG_OUT_PER_FILE_DIR = "config/enchantmentcontrol/out";

    public static void preInit(){
        //create EnchantmentInfo from json cfg files, wish i had Meldexun Forge Config Extension
        
        // Legacy enchantments.json support (read once, then rename to enchantments.legacy.unused)
        readLegacyConfigs();

        // Per-enchantment files (default): config/enchantmentcontrol/enchantments/<modid>/<enchid>.json
        try {
            List<EnchantmentInfo> ignored = readPerFileConfigs();
        } catch (Exception e){
            EnchantmentControl.LOGGER.warn("Reading enchantment configs failed!");
            e.printStackTrace(System.out);
        }
    }

    public static void postInit(){
        if (!ConfigHandler.printDefaults) return;
        try {
            writeAllCurrentEnchantmentInfos();
        } catch (IOException e) {
            EnchantmentControl.LOGGER.warn("Writing enchantment defaults failed!");
            e.printStackTrace(System.out);
        }
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

    private static void writeAllCurrentEnchantmentInfos() throws IOException {
        // Write one file per enchantment into config/enchantmentcontrol/out/<modid>/<enchid>.json
        File baseOut = new File(ENCH_CFG_OUT_PER_FILE_DIR);
        if (!baseOut.exists() && !baseOut.mkdirs()) {
            EnchantmentControl.LOGGER.warn("Could not create output directory: {}", baseOut.getPath());
        }

        for (EnchantmentInfo info : EnchantmentInfo.getAll()) {
            String id = EnchantmentInfo.getEnchantmentId(info);
            String[] split = id.split(":");
            String modid = split[0];
            String enchid = split[1];

            File modDir = new File(baseOut, modid);
            if (!modDir.exists() && !modDir.mkdirs()) {
                EnchantmentControl.LOGGER.warn("Could not create mod output directory: {}", modDir.getPath());
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
    }

    private static List<EnchantmentInfo> readPerFileConfigs() {
        File base = new File(ENCH_CFG_PER_FILE_DIR);
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
        List<EnchantmentInfo> infos = new ArrayList<>();

        File legacyFile = new File(ENCH_CFG_PATH);
        if (legacyFile.exists() && legacyFile.isFile()) {
            try (InputStream in = Files.newInputStream(legacyFile.toPath())) {
                infos.addAll(readListWithGson(in));

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
