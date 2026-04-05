package enchantmentcontrol.config.folders;

import enchantmentcontrol.EnchantmentControl;
import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;

@MixinConfig(name = EnchantmentControl.MODID)
public class EnchTableConfig {
    @Config.Comment({
            "Max level the vanilla enchanting table can roll. ",
            "Only works in steps of 2.",
            "Set to -1 to disable mixin (requires restart)",
            "Incompatible with apotheosis"
    })
    @Config.Name("(MixinToggle) Enchantment Table Max Lvl")
    @Config.RangeInt(min = -1)
    public int maxLvl = 30;

    @Config.Comment({
            "Restores older minecraft versions behavior of rolling new enchantments whenever the item gets placed fresh into the enchantment table.",
            "Incompatible with apotheosis"
    })
    @Config.Name("(MixinToggle) Always reroll table")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.enchantmentcontrol.vanilla.etablealwaysreroll.json", defaultValue = false)
    @MixinConfig.CompatHandling(modid = "apotheosis", desired = false, warnIngame = false, reason = "Incompatible with Apotheosis")
    public boolean alwaysReroll = false;

    @Config.Comment("Shows the enchantment clue directly in the GUI of the enchantment table instead of needing to hover.")
    @Config.Name("(MixinToggle) Preview Enchantment Clue")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.enchantmentcontrol.vanilla.etablepreviewclue.json", defaultValue = false)
    public boolean previewClue = false;

    @Config.Comment({
            "Allows to enchant already enchanted items on enchanting table.",
            "This will add to existing enchants.",
            "Incompatible with apotheosis & noexpensive"
    })
    @Config.Name("(MixinToggle) Allow Re-Enchant")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.enchantmentcontrol.vanilla.etablereenchant.json", defaultValue = false)
    @MixinConfig.CompatHandling(modid = "apotheosis", desired = false, warnIngame = false, reason = "Incompatible with Apotheosis")
    @MixinConfig.CompatHandling(modid = "noexpensive", desired = false, warnIngame = false, reason = "Incompatible with No Expensive")
    public boolean allowReEnchant = true;

    @Config.Comment({
            "The Chance to skip incrementing the enchant count when enchanting an item.",
            "To add a chance to be allowed to re-enchant more than expected",
            "Requires \"Allow Re-Enchant\""
    })
    @Config.Name("Re-Enchant Count Skip Increment Chance")
    @Config.SlidingOption
    @Config.RangeDouble(min = 0, max = 1)
    public float reEnchantSkipIncrementChance = 0.2F;

    @Config.Comment({
            "How often items can be (re-) enchanted with the enchanting table.",
            "NOTE: items enchanted through other means (loot etc) will start at 0 uses just like unenchanted crafted items",
            "Set to 0 to not allow anything to be enchanted",
            "Set to 1 to allow re-enchanting pre-enchanted loot once",
            "Requires \"Allow Re-Enchant\""
    })
    @Config.Name("Re-Enchant Max Count")
    @Config.RangeInt(min = 0)
    public int reEnchantMaxTimes = 1;

    @Config.Comment({
            "Whether to consider enchants being incompatible with each other when enchanting an item again in the enchanting table.",
            "Requires \"Allow Re-Enchant\""
    })
    @Config.Name("Re-Enchant Allow Incompatible")
    public boolean reEnchantAllowIncompatible = false;

    @Config.Comment({
            "Whether to combine lvls of same enchantments when enchanting an item again in the enchanting table.",
            "NOTE: if the all rolled enchants already are on the item, the enchantment wont be possible.",
            "Requires \"Allow Re-Enchant\""
    })
    @Config.Name("Re-Enchant Combine Existing")
    public boolean reEnchantCombineExisting = false;
}
