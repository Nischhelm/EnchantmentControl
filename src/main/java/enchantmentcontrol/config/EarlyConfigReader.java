package enchantmentcontrol.config;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class EarlyConfigReader {
    public static final String BLACKLIST_CONFIG_NAME = "Blacklisted Enchantment Classes";
    public static final String IDREMAP_CONFIG_NAME = "Enchantment Id Remaps";
    public static final String CONFIG_PATH = "config/enchantmentcontrol.cfg";

    private static Set<String> blacklistConfig_earlyAccess = null;
    private static Map<String, ResourceLocation> remapConfig_earlyAccess = null;

    public static Set<String> readConfigForBlacklist(){
        if(blacklistConfig_earlyAccess == null) {
            blacklistConfig_earlyAccess = new HashSet<>();
            Path enchclasses_path = Paths.get(CONFIG_PATH);
            try {
                Files.createDirectories(enchclasses_path.getParent());

                boolean isReading = false;
                for (String line : Files.readAllLines(enchclasses_path)) {
                    if (line.contains("S:\""+ BLACKLIST_CONFIG_NAME +"\"")) {
                        isReading = true;
                        continue;
                    }
                    if (!isReading) continue; //unimportant lines
                    if (line.contains(">")) break; //End of bracket

                    blacklistConfig_earlyAccess.add(line.trim());
                }
            } catch (IOException ignored) {}
        }

        return blacklistConfig_earlyAccess;
    }

    @Nullable
    public static ResourceLocation getRemap(String in){
        return readConfigForRemaps().get(in);
    }

    public static Map<String, ResourceLocation> readConfigForRemaps(){
        if(remapConfig_earlyAccess == null) {
            remapConfig_earlyAccess = new HashMap<>();
            Path enchclasses_path = Paths.get(CONFIG_PATH);
            try {
                Files.createDirectories(enchclasses_path.getParent());

                boolean isReading = false;
                for (String line : Files.readAllLines(enchclasses_path)) {
                    //"enchantment id remaps" {
                    if (line.contains("\""+ IDREMAP_CONFIG_NAME.toLowerCase(Locale.ROOT)+"\" {")) {
                        isReading = true;
                        continue;
                    }
                    if (!isReading) continue; //unimportant lines

                    if (line.contains("}")) break; //end of bracket

                    String[] split = line.split("\"=");
                    remapConfig_earlyAccess.put(split[0].replaceFirst("S:\"","").trim(),new ResourceLocation(split[1].trim()));
                }
            } catch (IOException ignored) {}
        }

        return remapConfig_earlyAccess;
    }
}
