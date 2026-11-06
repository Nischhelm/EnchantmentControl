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
    @MixinConfig.MixinToggle(earlyMixin = "mixins.enchantmentcontrol.vanilla.firstenchanttooltip.json", defaultValue = true)
    public boolean renderFirstEnchantUnderscored = true;

    @Config.Comment("Fix enchant_randomly loot function deliberately soft crashing if an enchant is not found.")
    @Config.Name("(MixinToggle) Fix EnchantRandomly Crash")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.enchantmentcontrol.vanilla.fixloottablecrash.json", defaultValue = true)
    public boolean fixEnchantRandomlyCrash = true;

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
