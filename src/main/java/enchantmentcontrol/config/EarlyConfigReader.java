package enchantmentcontrol.config;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.util.ConfigRef;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EarlyConfigReader {
    public static final String CONFIG_PATH = "config/enchantmentcontrol.cfg";

    private static Set<String> blacklistConfig = null;
    private static Map<String, ResourceLocation> remapConfig = null;
    private static Map<String, Integer> rarityConfig = null;
    private static List<String> registrationBlacklist = null;

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

    public static List<String> getRegistrationBlacklist(){
        if(registrationBlacklist == null)
            registrationBlacklist = readConfigList(ConfigRef.REGISTRY_BLACKLIST_CONFIG_NAME, Function.identity());

        return registrationBlacklist;
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

            found = true;

            if (line.contains("}")) break; //end of bracket

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

    public static <V> List<V> readConfigList(String name, Function<String, V> valueMapper) {
        List<V> output = new ArrayList<>();

        boolean isReading = false;
        String nameToCheckFor = (name.contains(" ") ? "\"" + name + "\"" : name) + " <";

        boolean found = false;
        for (String line : readLines()) {
            if (line.contains(nameToCheckFor)) {
                isReading = true;
                continue;
            }
            if (!isReading) continue; //unimportant lines

            found = true;

            if (line.contains(">")) break; //end of bracket

            output.add(valueMapper.apply(line.trim()));
        }
        if (!found) EnchantmentControl.LOGGER.warn("Didnt find config list to early-read for {}", name);

        return output;
    }
    private static File configFile = null;
    private static String configBooleanString = null;
    private static String configIntString = null;
    private static String configDoubleString = null;
    private static Map<String,Boolean> configArrayFilledMap = null;

    public static boolean getBoolean(String name, boolean defaultValue) {
        if (configFile == null) configFile = new File("config", EnchantmentControl.MODID + ".cfg");

        if (configBooleanString == null) {
            if (configFile.exists() && configFile.isFile()) {
                try (Stream<String> stream = Files.lines(configFile.toPath())) {
                    //All lines starting with "B:"
                    configBooleanString = stream.filter(s -> s.trim().startsWith("B:")).collect(Collectors.joining());
                } catch (Exception e) {
                    EnchantmentControl.LOGGER.error("Failed to parse " + EnchantmentControl.NAME + " config: " + e);
                }
            } else configBooleanString = "";
        }

        if (configBooleanString.contains("B:\"" + name + "\"="))
            return configBooleanString.contains("B:\"" + name + "\"=true");
            //If config is not generated yet or missing entries, we use the default value that would be written into it
        else return defaultValue;
    }

    public static int getInt(String name, int defaultValue) {
        if (configFile == null) configFile = new File("config", EnchantmentControl.MODID + ".cfg");

        if (configIntString == null) {
            if (configFile.exists() && configFile.isFile()) {
                try (Stream<String> stream = Files.lines(configFile.toPath())) {
                    configIntString = stream.filter(s -> s.trim().startsWith("I:")).collect(Collectors.joining());
                } catch (Exception ex) {
                    EnchantmentControl.LOGGER.error("Failed to parse " + EnchantmentControl.NAME + " config: " + ex);
                }
            } else configIntString = "";
        }

        if (configIntString.contains("I:\"" + name + "\"=")) {
            int index = configIntString.indexOf("I:\"" + name + "\"=");
            try {
                Matcher matcher = Pattern.compile("(\\d+)").matcher(configIntString.substring(index));
                matcher.find();
                return Integer.parseInt(matcher.group(1));
            } catch (Exception e) {
                EnchantmentControl.LOGGER.error(EnchantmentControl.NAME + ": Failed to parse int config "+ name + ", " + e);
                return 0;
            }
        }
        //If config is not generated yet or missing entries, we use the default value that will get written into it right after this
        else return defaultValue;
    }

    public static double getDouble(String name, double defaultValue) {
        if (configFile == null) configFile = new File("config", EnchantmentControl.MODID + ".cfg");

        if (configDoubleString == null) {
            if (configFile.exists() && configFile.isFile()) {
                try (Stream<String> stream = Files.lines(configFile.toPath())) {
                    configDoubleString = stream.filter(s -> s.trim().startsWith("D:")).collect(Collectors.joining());
                } catch (Exception ex) {
                    EnchantmentControl.LOGGER.error("Failed to parse " + EnchantmentControl.NAME + " config: " + ex);
                }
            } else configDoubleString = "";
        }

        if (configDoubleString.contains("D:\"" + name + "\"=")) {
            int index = configDoubleString.indexOf("D:\"" + name + "\"=");
            try {
                int startindex = configDoubleString.indexOf("=", index)+1;
                int endindex = configDoubleString.indexOf("D\\:", startindex);
                return Double.parseDouble(configDoubleString.substring(startindex, endindex == -1 ? configDoubleString.length() : endindex).trim());
            } catch (Exception e) {
                EnchantmentControl.LOGGER.error(EnchantmentControl.NAME + ": Failed to parse double config {}, {}", name, e);
                return 0;
            }
        }
        //If config is not generated yet or missing entries, we use the default value that will get written into it right after this
        else return defaultValue;
    }

    public static boolean isArrayFilled(String name, boolean filledByDefault) {
        if (configFile == null) configFile = new File("config", EnchantmentControl.MODID + ".cfg");

        if (configArrayFilledMap == null) {
            configArrayFilledMap = new HashMap<>();
            if (configFile.exists() && configFile.isFile()) {
                try {
                    String allLines = Files.lines(configFile.toPath()).collect(Collectors.joining("\t")); //whitespace \s doesnt match \n here for whatever reason, otherwise i could easily use that for the config name special character
                    Matcher matcher = Pattern.compile("[SID]:\"([^\t]+)\" <([^>]*)>").matcher(allLines); //regex matches any config lines with any < ... > entry and a name allowing anything except tabspace (\t). had to use some special character no one would use in config name. idk if this one was a good one
                    while (matcher.find())
                        configArrayFilledMap.put(matcher.group(1), !matcher.group(2).matches("\\s*")); //save config name and whether its empty (only whitespace) or not
                } catch (Exception ex) {
                    EnchantmentControl.LOGGER.error("Failed to parse " + EnchantmentControl.NAME + " config: " + ex);
                }
            }
        }

        return configArrayFilledMap.getOrDefault(name, filledByDefault);
    }
}
