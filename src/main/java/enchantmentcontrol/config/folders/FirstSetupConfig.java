package enchantmentcontrol.config.folders;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.util.ConfigRef;
import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;

import java.util.HashMap;
import java.util.Map;

@MixinConfig(name = EnchantmentControl.MODID)
public class FirstSetupConfig {
    @Config.Comment("Enable if you want to remap enchantment ids. Warning: See Warning in \""+ConfigRef.IDREMAP_CONFIG_NAME+"\"")
    @Config.Name("Enable Registry Remapping (MixinToggle)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.enchantmentcontrol.vanilla.registryremap.json", defaultValue = false)
    public boolean enableRegistryRemap = false;

    @Config.Comment({
            "If you really dont like one mods enchantment id (modid:enchid), you can add it here with a second entry of what you want to call it instead (modid:enchid). ",
            "WARNING: this will cause permanent id remapping issues for old worlds! Only do this for new worlds or before you release a modpack, if you really need to!",
            "pattern: S:\"modid:oldenchid\"=modid:newenchid",
            "Has Mixin Toggle \"Enable Registry Remapping\""
    })
    @Config.Name(ConfigRef.IDREMAP_CONFIG_NAME)
    @Config.RequiresMcRestart
    public Map<String, String> idRemaps = new HashMap<String, String>(){};

    @Config.Comment({
            "!Disables itself after a one time use!",
            "",
            "If enabled, during startup this mod will infer info about all registered enchantments and print them out in /config/enchantmentcontrol/inferred/.",
            "The created json files can be used as blueprints from which to work off of.",
            "To do so, copy everything to /config/enchantmentcontrol/enchantments/, then delete every file + line in file that should stay default/untouched.",
            "DEBUG: These can also be used to check if the changes you apply to the enchantments are actually applied, which would reflect in the inferred files (except \"types\")",
            "WARNING: All files in /inferred/ will be overwritten every time you start the game with this option enabled"
    })
    @Config.Name(ConfigRef.DO_INFER_CONFIG_NAME)
    @Config.RequiresMcRestart
    public boolean printInferred = true;

    @Config.Comment({
            "Advanced",
            "",
            "When inferring cfg jsons from existing enchants, enabling this will also infer info that is usually not interesting to modify.",
            "This will additionally give info for",
            " - slots - rarely used system in what slot an enchant has to be in to be used",
            " - minLvl - basically always 1",
            " - doublePrice - to modify librarian price doubling independent of treasure property",
            " - enchantability - to modify at which lvl which enchant is available on enchanting table and connected systems",
            " - isAllowedOnBooks - a vanilla property, only used specifically when enchanting books with lvls, where those disallowed will never be rolled",
            "Every option that is not set in a json will automatically behave as default."
    })
    @Config.Name("Print Inferred Expanded")
    public boolean printInferredExpanded = false;

    @Config.Comment({
            "!Disables itself after a one time use!",
            "",
            "If enabled, during startup this mod will check all present incompatibilities between enchantments and print them out into the \"Incompatible Enchantment Groups\" config.",
            "Config will look correct after restart.",
            "This is done because this mod fully overwrites enchantment incompatibility, so this step should be done every time a new mod with enchantments is added to a modpack, to keep its inherent incompats."
    })
    @Config.Name(ConfigRef.PRINT_INCOMPAT_CONFIG_NAME)
    @Config.RequiresMcRestart
    public boolean printIncompats = true;

    @Config.Comment({
            "!Disables itself after a one time use!",
            "",
            "If enabled, during startup this mod will check various (not all!) item applicabilities for all registered enchantments and print them out into the \"Item Types\" config.",
            "Config will look correct after restart.",
            "This is done to make it easier to work off of default item types for people wanting to modify what enchantment can go on which items."
    })
    @Config.Name(ConfigRef.PRINT_TYPES_CONFIG_NAME)
    @Config.RequiresMcRestart
    public boolean readTypes = true;
}
