package enchantmentcontrol.config.folders;

import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;

public class AnvilConfig {
    @Config.Comment("Removes the \"Too Expensive\" system of anvils")
    @Config.Name("Never Too Expensive (MixinToggle)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.enchantmentcontrol.vanilla.anvilnoexpensive.json", defaultValue = false)
    @MixinConfig.CompatHandling(modid = "noexpensive", desired = false, warnIngame = false, reason = "Incompat with No Expensive") //TODO other incompats
    @SuppressWarnings("unused")
    public boolean enableNoExpensive = false;

    @Config.Comment("If enabled, using the anvil on an item without modifying its enchantments (repair with item or material, renames) will not increase its anvil use cost")
    @Config.Name("No Cost Increase On Repairs (MixinToggle)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.enchantmentcontrol.vanilla.anvilnoincreaseonrepairs.json", defaultValue = false)
    //TODO incompats
    @SuppressWarnings("unused")
    public boolean noCostIncreaseOnRepairs = false;

    @Config.Comment("Main toggle for the Anvil Use Cost: Scaling Type, Combination Type and Scaling Factor in this config to be applicable")
    @Config.Name("Change Cost Scaling (MixinToggle)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.enchantmentcontrol.vanilla.anvilscaling.json", defaultValue = false)
    @SuppressWarnings("unused")
    public boolean enableAnvilScalingChange = false;

    @Config.Comment({
            "How Anvil Use Cost (\"RepairCost\") scales with increased uses of the anvil",
            "Available Types:",
            "  EXPONENTIAL (a^n) - 1: Default system, possibly with modified scaling factor. Every use multiplies the current cost by the given factor (-1)",
            "  QUADRATIC (a x n²): Less steep increase on more uses. With a factor of 1 it's similar to exponential but doesn't go as crazy for higher uses",
            "  LINEAR (a x n): Slow increase. Every use adds the given factor to the current cost.",
            "  CONST (a): No increase. Items that have been used in the anvil previously will have a fixed additional cost of the given factor. Items never used in the anvil before will have no additional cost.",
            "In all of those, a is the \"Scaling Factor\" defined here and n is the current use count",
            "Personal suggestion to use QUADRATIC with factor 1. Extremely close to default for anvil uses of 0-5, noticeably less crazy increase for higher uses",
            "Default: 0, 1, 3, 7, 15, 31, 63, 127, 255, 512, 1023",
            "Suggested quadratic: 0, 1, 4, 9, 16, 25, 36, 49, 64, 81, 100"
    })
    @Config.Name("Anvil Use Cost Scaling Type")
    public ScalingType repairCostScalingType = ScalingType.EXPONENTIAL;
    public enum ScalingType { EXPONENTIAL, QUADRATIC, LINEAR, CONST}

    @Config.Comment({
            "Scaling factor \"a\" used in combination with the \"Scaling Type\" defined here.",
            "Default is EXPONENTIAL with a factor of 2"
    })
    @Config.Name("Anvil Use Cost Scaling Factor")
    public float repairCostScalingFactor = 2;

    @Config.Comment({
            "How the Anvil Use Cost of two items that are combined in the anvil get combined for the resulting item",
            "  MAX: The default system. The resulting item will use the bigger anvil use cost of the two ingredient items (+1)",
            "  SUM: Will add up the two items use cost (+1)",
            "  AVERAGE: Will average the two items use cost (+1)",
            "  MIN: Will take the smaller anvil use cost of the two ingredient items (+1). Probably not a great idea to use this one but who knows.",
            "Note: This will also apply to the anvil use COUNT used to calc the anvil use COST"
    })
    @Config.Name("Anvil Use Cost Combination Type")
    public ComboType repairCostCombinationType = ComboType.MAX;
    public enum ComboType { MIN, MAX, SUM, AVERAGE }
}
