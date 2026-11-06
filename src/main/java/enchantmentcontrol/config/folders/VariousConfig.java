package enchantmentcontrol.config.folders;

import enchantmentcontrol.Tags;
import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;

@MixinConfig(name = Tags.MODID)
@SuppressWarnings("unused")
public class VariousConfig {
    @Config.Comment("When rendering enchantments on an ItemStack, render the first one in underscored")
    @Config.Name("(MixinToggle) Render First Enchant Underscored")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(mixinJson = "mixins.enchantmentcontrol.vanilla.firstenchanttooltip.json", defaultValue = true)
    public boolean renderFirstEnchantUnderscored = true;

    @Config.Comment("Fix enchant_randomly loot function deliberately soft crashing if an enchant is not found.")
    @Config.Name("(MixinToggle) Fix EnchantRandomly Crash")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(mixinJson = "mixins.enchantmentcontrol.vanilla.fixloottablecrash.json", defaultValue = true)
    public boolean fixEnchantRandomlyCrash = true;

    @Config.Comment({
            "Displays enchantment levels without a lang entry (vanilla only has 1-10) as numbers, instead of \"enchantment.level.11\".",
            "Levels with a lang entry keep using it, so resource packs can still change them"
    })
    @Config.Name("(MixinToggle) Fix Enchantment Level Display")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(mixinJson = "mixins.enchantmentcontrol.vanilla.leveldisplay.json", defaultValue = true)
    public boolean fixEnchantmentLevelDisplay = true;

    @Config.Comment({
            "Allows to draw bows enchanted with Infinity without having any arrows in the inventory.",
            "Vanilla Infinity already shoots without consuming arrows, but still requires one to be in the inventory to start drawing the bow.",
            "Note: Only affects bows that don't override the vanilla bow drawing logic"
    })
    @Config.Name("(MixinToggle) Infinity Needs No Arrow")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(mixinJson = "mixins.enchantmentcontrol.vanilla.infinitynoarrow.json", defaultValue = true)
    public boolean infinityNeedsNoArrow = true;

    @Config.Comment({
            "If an item somehow has the same enchantment multiple times (Sharpness 3 and Sharpness 4), it will work as one enchantment with the summed up level (Sharpness 7).",
            "Vanilla is inconsistent there: lvl checks take the first entry, damage/protection modifiers and on hit effects apply each entry separately",
            "  and anything reading all enchantments at once (like the anvil) only sees the last entry.",
            "Note: Because of the latter, using such an item in the anvil (or blood anvil) will merge the entries into one with the summed up level."
    })
    @Config.Name("(MixinToggle) Sum Duplicate Enchantments")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(mixinJson = "mixins.enchantmentcontrol.vanilla.sumduplicateenchants.json", defaultValue = false)
    public boolean sumDuplicateEnchants = false;

    @Config.Comment({
            "If enabled, will register a loot function named set_enchantments.",
            "This loot function takes in a list of enchantment ids and random value ranges (or fixed values)",
            "And applies exactly these enchantments onto a given item (books are turned into enchanted books).",
            "Nothing is checked: not whether the enchantment can go on the item, not whether the enchantments are compatible with each other",
            "  and not whether the level is within the enchantments min/max lvl. So levels above max lvl (or below min lvl) are possible.",
            "Example showing all options: ",
            "\"function\": \"set_enchantments\",",
            "\"enchantments\":[{",
            "  \"enchantment\": \"minecraft:power\",",
            "  \"lvl\": 5",
            "},{",
            "  \"enchantment\": \"minecraft:punch\",",
            "  \"lvlMin\": 1,",
            "  \"lvlMax\": 2",
            "}]"
    })
    @Config.Name("Register SetEnchantments Loot Function")
    @Config.RequiresMcRestart
    public boolean enableSetEnchantmentsLootFunction = false;
}
