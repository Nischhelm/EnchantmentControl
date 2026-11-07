package enchantmentcontrol.compat.universaltweaks;

import enchantmentcontrol.Tags;
import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.launchwrapper.Launch;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs at coremod construction. UT only evaluates UT's mixin toggles later,
 * and UTConfigTweaks is only loaded at that point, so editing the cfg file here keeps the conflicting mixins from ever being queued.
 */
public class UniversalTweaksConfigFixer {
    private static final String FILE_NAME = "Universal Tweaks - Tweaks.cfg";

    public static void disableConflictingOptions() {
        if (!ConfigHandler.compat.universalTweaks.disableConflicting) return;

        File file = new File(new File(Launch.minecraftHome, "config"), FILE_NAME);
        if (!file.isFile()) return; //UT defaults don't conflict

        try {
            String original = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            String content = original;

            if (ConfigHandler.anvil.costs.enableNoExpensive)
                content = setValue(content, "I", "Anvil XP Level Cap", "40");
            if (ConfigHandler.etable.noAirCond)
                content = setValue(content, "B", "No Enchantment Table Obstruction", "false");

            if (!content.equals(original)) {
                Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
                Tags.LOGGER.warn("Disabled Universal Tweaks options conflicting with Enchantment Control (Anvil XP Level Cap / No Enchantment Table Obstruction) in {}", FILE_NAME);
            }
        } catch (IOException e) {
            Tags.LOGGER.error("Failed to disable conflicting Universal Tweaks options in {}", FILE_NAME, e);
        }
    }

    private static String setValue(String content, String typePrefix, String name, String value) {
        Pattern pattern = Pattern.compile("^(\\s*" + typePrefix + ":\"" + Pattern.quote(name) + "\"=)(.*?)(\\r?)$", Pattern.MULTILINE);
        return pattern.matcher(content).replaceAll("$1" + Matcher.quoteReplacement(value) + "$3");
    }
}
