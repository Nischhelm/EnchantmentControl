package enchantmentcontrol.config.folders;

import enchantmentcontrol.Tags;
import fermiumbooter.annotations.MixinConfig;
import meldexun.betterconfig.api.Order;
import net.minecraftforge.common.config.Config;

public class CompatConfig {

    @Config.Name("Just Enough Items")
    public JeiCompatConfig jei = new JeiCompatConfig();

    public static class JeiCompatConfig {
        @Config.Comment("Global toggle for enchantment info JEI Plugin")
        @Config.Name("Enabled Enchantment Info Plugin")
        @Config.RequiresMcRestart
        @Order(0)
        public boolean jeiInfoEnabled = true;

        @Config.Comment("Adds information about rarity, treasure and curse status to enchanted books in JEI")
        @Config.Name("Add JEI Rarity, Trasure, Curse Info")
        @Config.RequiresMcRestart
        @Order(1)
        public boolean jeiRarityTreasureInfo = true;

        @Config.Comment("Adds information about incompatible enchantments to enchanted books in JEI")
        @Config.Name("Add JEI Incompat Info")
        @Config.RequiresMcRestart
        @Order(2)
        public boolean jeiIncompatInfo = true;

        @Config.Comment("Adds information about item types to enchanted books in JEI")
        @Config.Name("Add JEI Item Types Info")
        @Config.RequiresMcRestart
        @Order(3)
        public boolean jeiItemTypesInfo = true;

        @Config.Comment("Adds information about which equipment slots enchantments are searched at to enchanted books in JEI")
        @Config.Name("Add JEI Applicable Slot Id Info")
        @Config.RequiresMcRestart
        @Order(4)
        public boolean jeiSlotInfo = false;

        @Config.Comment("Adds information about xp price (when on book/when on item) on anvil and potential emerald price range on Librarians to enchanted books in JEI")
        @Config.Name("Add JEI XP & Emerald Price Info")
        @Config.RequiresMcRestart
        @Order(5)
        public boolean jeiXPEmeraldPriceInfo = false;

        @Config.Comment("Adds information about enchantability levels to enchanted books in JEI")
        @Config.Name("Add JEI Enchantability Info")
        @Config.RequiresMcRestart
        @Order(6)
        public boolean jeiEnchantabilityInfo = true;

        @Config.Comment("Adds information about enchantment descriptions to enchanted books in JEI")
        @Config.Name("Add JEI Enchantment Description Info")
        @Config.RequiresMcRestart
        @Order(7)
        public boolean jeiDescriptionInfo = true;

        @Config.Comment("Adds information about enchantment id (modid:enchid) to enchanted books in JEI")
        @Config.Name("Add JEI Enchantment Id Info")
        @Config.RequiresMcRestart
        @Order(8)
        public boolean jeiEnchIdInfo = true;
    }

    @Config.Name("So Many Enchantments 1.0.0+")
    public NewSMECompat newSME = new NewSMECompat();

    @MixinConfig(name = Tags.MODID)
    public static class NewSMECompat {
        @Config.Comment({
                "Sets Anvil Use Count to 0 if Upgraded Potentials is used on an enchanted items.",
                "This is needed if \"Anvil Costs.Anvil Use Cost.Scaling Type\" is not the default EXPONENTIAL.",
                "Accounts for the cut-off at high anvil use costs (>~120 lvls) of UpgPot."
        })
        @Config.Name("(MixinToggle) Anvil Use Count UpgPot Compat")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(lateMixin = "mixins.enchantmentcontrol.somanyenchantments.json", defaultValue = true)
        @MixinConfig.CompatHandling(modid = "somanyenchantments", desired = true, warnIngame = false, reason = "Optional Compat for Upgraded Potentials if new SME is present", targetVersionRange = "[1.0.0,)")
        @SuppressWarnings("unused")
        public boolean upgradedPotentialsResetsAnvilCount = true;
    }

    @Config.Name("Enchantment Descriptions")
    public EnchDescCompat enchDesc = new EnchDescCompat();

    @MixinConfig(name = Tags.MODID)
    public static class EnchDescCompat {
        @Config.Comment({
                "Allow to use '\\n' in enchantment descriptions to force a newline.",
                "Can both be used in the descriptions.txt as well as in actual lang files for enchantment descriptions."
        })
        @Config.Name("(MixinToggle) Allow Line Breaks")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(lateMixin = "mixins.enchantmentcontrol.enchdesc.json", defaultValue = true)
        @MixinConfig.CompatHandling(modid = "enchdesc", desired = true, warnIngame = false, reason = "Optional Compat for Enchantment Descriptions")
        public boolean addLineBreaks = true;
    }

    @Config.Name("Enchanter Tools")
    public EnchanterToolsCompat enchanterTools = new EnchanterToolsCompat();

    @MixinConfig(name = Tags.MODID)
    public static class EnchanterToolsCompat {
        @Config.Comment({
                "Makes Lapis Runes work correctly with Re-Enchanting.",
                "One thing doesn't work: Combining existing levels with new rolled enchants (\"Re-Enchanting.Combine Existing\").",
                "I'm too lazy to fix that. Existing enchants will not be combined with new ones when using Lapis Runes."
        })
        @Config.Name("(MixinToggle) Re-Enchant Lapis Runes")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(lateMixin = "mixins.enchantmentcontrol.enchantertools.lapisrune.json", defaultValue = true)
        @MixinConfig.CompatHandling(modid = "enchanter_tools", desired = true, warnIngame = false, reason = "Optional Compat for Enchantment Descriptions")
        public boolean fixLapisRuneReenchanting = true;
    }

    @Config.Name("No Expensive")
    public NoExpensiveCompat noExpensive = new NoExpensiveCompat();

    public static class NoExpensiveCompat {
        @Config.Comment({
                "No Expensive overwrites the whole anvil logic, which breaks most anvil features of this mod.",
                "If this config is enabled, it disables No Expensive entirely. Its main feature is covered by \"Anvil Mechanics.Anvil Costs.(MixinToggle) Never Too Expensive\" (enabled by default).",
                "Differences to No Expensive:",
                " - No Expensive caps the anvil cost at 39 lvls, while this mod just removes the cap",
                " - No Expensive makes Infinity and Mending compatible in the anvil. To get that, remove that incompatible group from the \"Incompatible Groups\" config instead.",
                "If disabled, No Expensive stays. It overwrites the anvil logic that these options of this mod inject into, so disable them yourself, otherwise the game will crash:",
                " \"Anvil Mechanics.Anvil Costs.(MixinToggle) Never Too Expensive\"",
                " \"Anvil Mechanics.Anvil Costs.Anvil Use Cost.(MixinToggle) Change Scaling\"",
                " \"Anvil Mechanics.Anvil Costs.Anvil Use Cost.(MixinToggle) No Increase On Repairs\"",
                " \"Enchantment Table Mechanics.Re-Enchanting.(MixinToggle) Allow Re-Enchant\""
        })
        @Config.Name("Replace No Expensive")
        @Config.RequiresMcRestart
        public boolean replaceNoExpensive = true;
    }

    @Config.Name("Merge Enchantments")
    public MergeEnchantmentsCompat mergeEnchantments = new MergeEnchantmentsCompat();

    public static class MergeEnchantmentsCompat {
        @Config.Comment({
                "Merge Enchantments always overrides which enchantments are compatible with each other, which makes \"Incompatible Groups\" do nothing.",
                "With its \"combineEnchantments\" option, it also allows every enchantment on every item in the anvil, ignoring the anvil \"Item Types\".",
                "If enabled, disables Merge Enchantments entirely. Everything it does can be set up with this mod instead:",
                " - By default, it makes all enchantments compatible with each other except Silk Touch and Fortune (and its own blacklist).",
                "   To get that, only keep those in \"Incompatible Groups\" (or disable \"Incompatible Groups Enabled\" for no incompatibilities at all).",
                " - \"combineEnchantments\" = all enchants compatible and applicable on everything in the anvil: disable \"Incompatible Groups Enabled\" and add the enchants to \"ANY\" in \"Item Types.Anvil.Item Types\""
        })
        @Config.Name("Disable Merge Enchantments")
        @Config.RequiresMcRestart
        public boolean disableMergeEnchantments = true;
    }
}
