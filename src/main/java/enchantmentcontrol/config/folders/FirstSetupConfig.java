package enchantmentcontrol.config.folders;

import enchantmentcontrol.config.EarlyConfigReader;
import net.minecraftforge.common.config.Config;

import java.util.HashMap;
import java.util.Map;

public class FirstSetupConfig {
    @Config.Comment("If you really dont like one mods enchantment id (modid:enchid), you can add it here with a second entry of what you want to call it instead (modid:enchid). \n" +
            "WARNING: this will cause permanent id remapping issues for old worlds! Only do this for new worlds or before you release a modpack, if you really need to!\n" +
            "pattern: S:\"modid:oldenchid\"=modid:newenchid")
    @Config.Name(EarlyConfigReader.IDREMAP_CONFIG_NAME)
    @Config.RequiresMcRestart
    public Map<String, String> idRemaps = new HashMap<String, String>(){};

    @Config.Comment("!Disables itself after a one time use!\n" +
            "If enabled, during startup this mod will infer info about all registered enchantments and print them out in /config/enchantmentcontrol/inferred/.\n" +
            "The created files can be used as blueprints from which to work off of.\n" +
            "To do so, copy everything to /config/enchantmentcontrol/enchantments/, then delete every file + line in file that should stay default/untouched.\n" +
            "DEBUG: These can also be used to check if the changes you apply to the enchantments are actually applied, which would reflect in the inferred files (except \"types\")\n" +
            "WARNING: All files in /inferred/ will be overwritten every time you start the game with this option enabled")
    @Config.Name("Print Inferred Enchantment Infos")
    @Config.RequiresMcRestart
    public boolean printInferred = false;

    @Config.Comment("!Disables itself after a one time use!\n" +
            "If enabled, during startup this mod will check all present incompatibilities between enchantments and print them out into the \"Incompatible Enchantment Groups\" config.\n" +
            "Config will look correct after restart.\n" +
            "This is done because this mod fully overwrites enchantment incompatibility, so this step should be done every time a new mod with enchantments is added to a modpack, to keep its inherent incompats.")
    @Config.Name("Read and dump default incompats")
    @Config.RequiresMcRestart
    public boolean readIncompats = true;
}
