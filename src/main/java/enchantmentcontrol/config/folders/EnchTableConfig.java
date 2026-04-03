package enchantmentcontrol.config.folders;

import enchantmentcontrol.EnchantmentControl;
import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;

@MixinConfig(name = EnchantmentControl.MODID)
public class EnchTableConfig {
    @Config.Comment({
            "Max level the vanilla enchanting table can roll. ",
            "Only works in steps of 2.",
            "Set to -1 to disable (requires restart)"
    })
    @Config.Name("Enchantment Table Max Lvl")
    @Config.RangeInt(min = -1)
    public int maxLvl = 30;

    @Config.Comment("Restores older minecraft versions behavior of rolling new enchantments whenever the item gets placed fresh into the enchantment table.")
    @Config.Name("Always reroll table")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.enchantmentcontrol.vanilla.etablealwaysreroll.json", defaultValue = false)
    public boolean alwaysReroll = false;

    @Config.Comment("Shows the enchantment clue directly in the GUI of the enchantment table instead of needing to hover.")
    @Config.Name("Preview Enchantment Clue")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.enchantmentcontrol.vanilla.etablepreviewclue.json", defaultValue = false)
    public boolean previewClue = false;
}
