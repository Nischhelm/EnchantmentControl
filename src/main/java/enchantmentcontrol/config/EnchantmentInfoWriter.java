package enchantmentcontrol.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.util.EnchantmentInfo;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;

public class EnchantmentInfoWriter {
    public static final String ENCH_CFG_OUT_PER_FILE_DIR = "config/enchantmentcontrol/out";

    public static void postInit(){
        if (!ConfigHandler.dev.printDefaults) return;
        //TODO: figure out how/whether to use this
        writeAllCurrentEnchantmentInfos(EnchantmentInfo.getAll());
    }

    public static void writeAllCurrentEnchantmentInfos(Collection<EnchantmentInfo> infos) {
        // Write one file per enchantment into config/enchantmentcontrol/out/modid/enchid.json
        try {
            File baseOut = new File(ENCH_CFG_OUT_PER_FILE_DIR);
            if (!baseOut.exists() && !baseOut.mkdirs()) {
                EnchantmentControl.LOGGER.warn("Could not create output directory: {}", baseOut.getPath());
            }

            for (EnchantmentInfo info : infos) {
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
        } catch (IOException e) {
            EnchantmentControl.LOGGER.warn("Writing enchantment defaults failed!");
            e.printStackTrace(System.out);
        }
    }
}
