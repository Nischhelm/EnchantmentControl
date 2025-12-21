package enchantmentcontrol.config.folders;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.util.ConfigRef;
import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;

@MixinConfig(name = EnchantmentControl.MODID)
public class DebugConfig {
    @Config.Comment("Enchantment classes that should not be modified at all by this mod. \n" +
            "Use this if there are crashes when this mod tries to automatically modify some mods enchantments. \n" +
            "You can find the class name in config/enchantmentcontrol/tmp/enchclasses.dump\n" +
            "Classes noted here need to look like net.minecraft.enchantment.EnchantmentDamage\n" +
            "Vanilla enchants will always be targeted, so putting their classes in here won't do anything")
    @Config.Name(ConfigRef.BLACKLIST_CONFIG_NAME)
    @Config.RequiresMcRestart
    public String[] disabledClasses = {};

    @Config.Comment("If enabled, writes all currently loaded enchantment infos to /config/enchantmentcontrol/loaded/ during startup. Can be used to check if a given config json is actually loaded (and loaded correctly).")
    @Config.Name("Print Loaded Enchantment Infos")
    @Config.RequiresMcRestart
    public boolean printLoaded = false;

    @Config.Comment("This is just a safety switch if you really dislike the idea of this mod having a mixin straight into item registration (IForgeRegistryEntry.Impl.setRegistryName) and you dont use the enchantment id remapping feature anyway. It will save a few startup milliseconds for sure.")
    @Config.Name("Enable Registry Remapping (MixinToggle)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.enchantmentcontrol.vanilla.registryremap.json", defaultValue = true)
    public boolean enableRegistryRemap = true;

    @Config.Comment("Disable this to remove EnchantmentControls main feature which hooks into all registered enchantments code to modify how they behave. \n" +
            "Some features will still work. This is mainly meant for testing if this mods black magic mixins is responsible for a crash (hope not)")
    @Config.Name("Enable Enchantment Injection (MixinToggle)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(
            earlyMixin = "mixins.enchantmentcontrol.vanilla.main.json",
            lateMixin = "mixins.enchantmentcontrol.modded.json",
            defaultValue = true
    )
    public static boolean enableEnchantmentInjection = true;
}
