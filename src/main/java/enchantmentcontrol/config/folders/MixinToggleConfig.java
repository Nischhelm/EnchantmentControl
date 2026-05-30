package enchantmentcontrol.config.folders;

import enchantmentcontrol.EnchantmentControl;
import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;

@MixinConfig(name = EnchantmentControl.MODID)
@SuppressWarnings("unused")
public class MixinToggleConfig {
    @Config.Comment("When rendering enchantments on an ItemStack, render the first one in bold")
    @Config.Name("(MixinToggle) Render First Enchant Bold")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.enchantmentcontrol.vanilla.firstenchanttooltip.json", defaultValue = true)
    public boolean renderFirstEnchantBold = true;
}
