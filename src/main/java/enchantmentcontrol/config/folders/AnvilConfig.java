package enchantmentcontrol.config.folders;

import enchantmentcontrol.Tags;
import fermiumbooter.annotations.MixinConfig;
import meldexun.betterconfig.api.Order;
import net.minecraftforge.common.config.Config;

public class AnvilConfig {
    @Config.Comment("Options for the XP costs of anvil actions, including the Anvil Use Cost (\"RepairCost\") of items")
    @Config.Name("Anvil Costs")
    @Order(0)
    public AnvilCostConfig costs = new AnvilCostConfig();

    @Config.Comment("Options for the Blood Anvil")
    @Config.Name("Blood Anvil")
    @Order(1)
    public BloodAnvilConfig bloodAnvil = new BloodAnvilConfig();

    @Config.Comment("Options for an item that can repair any repairable item in the anvil")
    @Config.Name("Universal Repair Item")
    @Order(2)
    public UniversalRepairConfig universalRepair = new UniversalRepairConfig();

    @MixinConfig(name = Tags.MODID)
    public static class UniversalRepairConfig {
        @Config.Comment("If enabled, the given item can be used in the right anvil slot to repair any repairable item, similar to repair materials")
        @Config.Name("(MixinToggle) Enabled")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.enchantmentcontrol.vanilla.anviluniversalrepair.json", defaultValue = false)
        @SuppressWarnings("unused")
        @Order(0)
        public boolean enabled = false;

        @Config.Comment({
                "Registry name of the universal repair item, optionally with metadata",
                "Pattern: modid:itemname or modid:itemname:meta"
        })
        @Config.Name("Item Name")
        @Order(1)
        public String itemName = "minecraft:nether_star";

        @Config.Comment({
                "How much % of the max durability each universal repair item repairs. Default 0.5 = 50%",
                "Like with repair materials, multiple items in the stack can be used at once, each costing 1 lvl (if repairing isn't set to be free)"
        })
        @Config.Name("Repair Amount")
        @Config.RangeDouble(min = 0.001, max = 1)
        @Config.SlidingOption
        @Order(2)
        public float repairAmount = 0.5F;

        @Config.Comment({
                "Items that can't be repaired with the universal repair item.",
                "Entries with a colon are item registry names (modid:itemname), entries without a colon are item type names as defined in the \"Item Types\" section (for example SWORD or ARMOR)"
        })
        @Config.Name("Item Blacklist")
        @Order(3)
        public String[] itemBlacklist = {};
    }

    @MixinConfig(name = Tags.MODID)
    public static class AnvilCostConfig {
        @Config.Comment("Removes the \"Too Expensive\" system of anvils")
        @Config.Name("(MixinToggle) Never Too Expensive")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.enchantmentcontrol.vanilla.anvilnoexpensive.json", defaultValue = true)
        @SuppressWarnings("unused")
        @Order(0)
        public boolean enableNoExpensive = true;

        @Config.Comment("If enabled, adding enchants with lower level or with already max lvl (anything leaving the resulting lvl the same as the one on the left item) will cost 0 XP in the anvil.")
        @Config.Name("(MixinToggle) No cost if Enchantment Lvl unchanged")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.enchantmentcontrol.vanilla.zerocostifunchanged.json", defaultValue = false)
        @SuppressWarnings("unused")
        @Order(1)
        public boolean noCostIfUnchanged = false;

        @Config.Comment({
                "If enabled, renaming an item in the anvil will not cost any XP.",
                "If nothing else costs XP in that anvil action, the anvil use cost (\"RepairCost\") of the items isn't paid either, making plain renames completely free."
        })
        @Config.Name("(MixinToggle) Free Renames")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.enchantmentcontrol.vanilla.anvilfreerenames.json", defaultValue = false)
        @Order(2)
        public boolean freeRenames = false;

        @Config.Comment({
                "If enabled, repairing an item in the anvil will not cost any XP.",
                "This removes the 1 lvl per repair material used, as well as the 2 lvls for repairing with a second item of the same type.",
                "If nothing else costs XP in that anvil action, the anvil use cost (\"RepairCost\") of the items isn't paid either, making plain repairs completely free."
        })
        @Config.Name("(MixinToggle) Free Repairs")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.enchantmentcontrol.vanilla.anvilfreerepairs.json", defaultValue = false)
        @Order(3)
        public boolean freeRepairs = false;

        @Config.Comment({
                "If enabled, using the anvil will never require or cost any XP. The XP cost isn't displayed anymore either.",
                "Note: the anvil use cost (\"RepairCost\") of items still increases as usual and items can still become \"Too Expensive\" if enabled. You can disable these separately."
        })
        @Config.Name("(MixinToggle) Totally Free Anvil")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.enchantmentcontrol.vanilla.anvilnoxpcost.json", defaultValue = false)
        @MixinConfig.CompatHandling(modid = "anvilpatchevil", desired = false, warnIngame = false, reason = "Anvil Patch - Evil already does this")
        @Order(4)
        public boolean noXPCosts = false;

        @Config.Comment("Options for the Anvil Use Cost (\"RepairCost\"), the additional XP cost items accumulate every time they are used in the anvil")
        @Config.Name("Anvil Use Cost")
        @Order(5)
        public AnvilUseCostConfig useCost = new AnvilUseCostConfig();
    }

    @MixinConfig(name = Tags.MODID)
    public static class AnvilUseCostConfig {
        @Config.Comment("Main toggle for \"Scaling Type\", \"Scaling Factor\" and \"Combination Type\" to be applicable")
        @Config.Name("(MixinToggle) Change Scaling")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.enchantmentcontrol.vanilla.anvilscaling.json", defaultValue = false)
        @SuppressWarnings("unused")
        @Order(0)
        public boolean enableScalingChange = false;

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
                "Suggested quadratic: 0, 1, 4, 9, 16, 25, 36, 49, 64, 81, 100",
                "Requires \"(MixinToggle) Change Scaling\""
        })
        @Config.Name("Scaling Type")
        @Order(1)
        public ScalingType scalingType = ScalingType.EXPONENTIAL;
        public enum ScalingType { EXPONENTIAL, QUADRATIC, LINEAR, CONST}

        @Config.Comment({
                "Scaling factor \"a\" used in combination with the \"Scaling Type\" defined here.",
                "Default is EXPONENTIAL with a factor of 2",
                "Requires \"(MixinToggle) Change Scaling\""
        })
        @Config.Name("Scaling Factor")
        @Config.RangeDouble(min = Float.MIN_VALUE)
        @Order(2)
        public float scalingFactor = 2;

        @Config.Comment({
                "How the Anvil Use Cost of two items that are combined in the anvil get combined for the resulting item",
                "  MAX: The default system. The resulting item will use the bigger anvil use cost of the two ingredient items (+1 use)",
                "  SUM: Will add up the two items use cost (+1 use)",
                "  AVERAGE: Will average the two items use cost (+1 use)",
                "  MIN: Will take the smaller anvil use cost of the two ingredient items (+1 use). Probably not a great idea to use this one but who knows.",
                "Note: This will also apply to the anvil use COUNT used to calc the anvil use COST",
                "Requires \"(MixinToggle) Change Scaling\""
        })
        @Config.Name("Combination Type")
        @Order(3)
        public ComboType combinationType = ComboType.MAX;
        public enum ComboType { MIN, MAX, SUM, AVERAGE }

        @Config.Comment({
                "If set to true, removes anvil use cost increase when combining two single enchanted books with the same lvl (Prot 3 + Prot 3 = Prot 4)",
                "Note: This is copied and updated from SoManyEnchantments 1.0.8"
        })
        @Config.Name("(MixinToggle) Free Book Combination")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.enchantmentcontrol.vanilla.freebookcombination.json", defaultValue = false)
        @SuppressWarnings("unused")
        @Order(4)
        public boolean freeBookCombination = false;

        @Config.Comment("If enabled, using the anvil on an item without modifying its enchantments (repair with item or material, renames) will not increase its anvil use cost")
        @Config.Name("(MixinToggle) No Increase On Repairs")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.enchantmentcontrol.vanilla.anvilnoincreaseonrepairs.json", defaultValue = false)
        @SuppressWarnings("unused")
        @Order(5)
        public boolean noIncreaseOnRepairs = false;

        @Config.Comment("Options for the tooltip showing how often an item has been used in the anvil")
        @Config.Name("Tooltip")
        @Order(6)
        public AnvilUseTooltipConfig tooltip = new AnvilUseTooltipConfig();
    }

    public static class AnvilUseTooltipConfig {
        @Config.Comment("When in the anvil gui, adds a tooltip to items with anvil use cost to indicate how often they have been used at the anvil already. Only in advanced mode = F3+H")
        @Config.Name("Enabled")
        @Config.RequiresMcRestart
        @Order(0)
        public boolean enabled = true;

        @Config.Comment("When enabled, the anvil use tooltip will be shown everywhere, not just in the anvil GUI")
        @Config.Name("Show Everywhere")
        @Order(1)
        public boolean showEverywhere = false;

        @Config.Comment("When enabled, the anvil use tooltip will be shown when pressing Shift instead of requiring advanced mode (F3+H)")
        @Config.Name("On Shift Key")
        @Order(2)
        public boolean onShiftKey = false;
    }
}
