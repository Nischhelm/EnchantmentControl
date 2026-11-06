package enchantmentcontrol.config.folders;

import enchantmentcontrol.Tags;
import fermiumbooter.annotations.MixinConfig;
import meldexun.betterconfig.api.Order;
import net.minecraftforge.common.config.Config;

import java.util.ArrayList;

@MixinConfig(name = Tags.MODID)
public class DebugConfig {
    @Config.Comment("Disable this to remove EnchantmentControls main feature which hooks into all registered enchantments code to modify how they behave. \n" +
            "Some features will still work. This is mainly meant for testing if this mods black magic mixins is responsible for a crash (hope not)")
    @Config.Name("(ASMToggle) Enable Enchantment Injection")
    @Config.RequiresMcRestart
    @Order(0)
    public boolean enableEnchantmentInjection = true;

    @Config.Comment("Enchantment classes that should not be modified at all by this mod. \n" +
            "Use this if there are crashes when this mod tries to automatically modify some mods enchantments. \n" +
            "You can find the class name in config/enchantmentcontrol/tmp/enchclasses.dump\n" +
            "Class names noted here need to look like net.minecraft.enchantment.EnchantmentDamage\n" +
            "Vanilla enchants will always be targeted, so putting their classes in here won't do anything")
    @Config.Name("Blacklisted Enchantment Classes")
    @Config.RequiresMcRestart
    @Order(1)
    public ArrayList<String> disabledClasses = new ArrayList<>();

    @Config.Comment("If enabled, writes all currently loaded enchantment infos to /config/enchantmentcontrol/loaded/ during startup. Can be used to check if a given config json is actually loaded (and loaded correctly).")
    @Config.Name("Print Loaded Enchantment Infos")
    @Config.RequiresMcRestart
    @Order(2)
    public boolean printLoaded = false;
}
