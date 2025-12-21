package enchantmentcontrol.config;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.util.ConfigRef;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

public class EarlyConfigReader {
    public static final String CONFIG_PATH = "config/enchantmentcontrol.cfg";

    private static Set<String> blacklistConfig = null;
    private static Map<String, ResourceLocation> remapConfig = null;
    private static Map<String, Integer> rarityConfig = null;

    private static List<String> lines = null;
    private static List<String> readLines(){
        if(lines == null){
            lines = new ArrayList<>();
            Path enchclasses_path = Paths.get(CONFIG_PATH);
            try {
                Files.createDirectories(enchclasses_path.getParent());
                lines.addAll(Files.readAllLines(enchclasses_path));
            } catch (IOException ignored) {}
        }
        return lines;
    }
    public static void clearLines(){
        lines = null;
    }

    public static Set<String> getClassBlacklistConfig(){
        if(blacklistConfig == null) {
            blacklistConfig = new HashSet<>();
            boolean isReading = false;
            for (String line : readLines()) {
                if (line.contains("S:\""+ ConfigRef.BLACKLIST_CONFIG_NAME +"\"")) {
                    isReading = true;
                    continue;
                }
                if (!isReading) continue; //unimportant lines
                if (line.contains(">")) break; //End of bracket

                blacklistConfig.add(line.trim());
            }
        }

        return blacklistConfig;
    }

    @Nullable
    public static ResourceLocation getRemap(String in){
        return getRemapConfig().get(in);
    }

    public static Map<String, ResourceLocation> getRemapConfig(){
        if(remapConfig == null)
            remapConfig = readConfigMap(ConfigRef.IDREMAP_CONFIG_NAME, Function.identity(), ResourceLocation::new);

        return remapConfig;
    }

    public static Map<String, Integer> getRarityConfig(){
        if(rarityConfig == null)
            rarityConfig = readConfigMap(ConfigRef.RARITY_CONFIG_NAME, Function.identity(), Integer::parseInt);

        return rarityConfig;
    }

    public static <I, O> Map<I,O> readConfigMap(String name, Function<String, I> inputMapper, Function<String, O> outputMapper) {
        Map<I, O> output = new HashMap<>();

        boolean isReading = false;
        String nameToCheckFor = (name.contains(" ") ? "\"" + name + "\"" : name) + " {";
        nameToCheckFor = nameToCheckFor.toLowerCase(Locale.ROOT);

        boolean found = false;
        for (String line : readLines()) {
            if (line.contains(nameToCheckFor)) {
                isReading = true;
                continue;
            }
            if (!isReading) continue; //unimportant lines

            if (line.contains("}")) break; //end of bracket

            found = true;

            String[] split = line.split("=");
            if (split.length != 2) {
                EnchantmentControl.LOGGER.warn("Unable to early-read config map for {} at line {}, expected X:\"somestring\"=val", name, line);
                continue;
            }
            String input = split[0].trim().substring(2); //remove X:
            boolean hasQuotes = input.startsWith("\"") && input.endsWith("\"");
            if (hasQuotes) input = input.substring(1, input.length() - 1); //remove quotes

            output.put(inputMapper.apply(input.trim()), outputMapper.apply(split[1].trim()));
        }
        if (!found) EnchantmentControl.LOGGER.warn("Didnt find config map to early-read for {}", name);

        return output;
    }
}
