package enchantmentcontrol.config.folders;

import enchantmentcontrol.config.EarlyConfigReader;
import net.minecraftforge.common.config.Config;

public class DebugConfig {
    @Config.Comment("Enchantment classes that should not be modified at all by this mod. \n" +
            "Use this if there are crashes when this mod tries to automatically modify some mods enchantments. \n" +
            "You can find the class name in config/enchantmentcontrol/tmp/enchclasses.dump\n" +
            "Classes noted here need to look like net.minecraft.enchantment.EnchantmentDamage\n" +
            "Vanilla enchants will always be targeted, so putting their classes in here won't do anything")
    @Config.Name(EarlyConfigReader.BLACKLIST_CONFIG_NAME)
    @Config.RequiresMcRestart
    public String[] disabledClasses = {};

    @Config.Comment("If enabled, writes all currently loaded enchantment infos to /config/enchantmentcontrol/loaded/ during startup. Can be used to check if a given config json is actually loaded (and loaded correctly).")
    @Config.Name("Print Loaded Enchantment Infos")
    @Config.RequiresMcRestart
    public boolean printLoaded = false;
}
