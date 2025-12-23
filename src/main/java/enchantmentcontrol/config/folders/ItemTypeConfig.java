package enchantmentcontrol.config.folders;

import net.minecraftforge.common.config.Config;

public class ItemTypeConfig {
    @Config.Comment({
            "Enchantments are allowed on items using matchers. Define custom matchers here.",
            "Pattern: MatcherName, Type, Arguments",
            "Available Types with Arguments:",
            "  modid, somemodid",
            "  regex, someregex",
            "  items, mymod:mysword",
            "  items, minecraft:iron_sword, mymod:mysword, ...",
            "  class, some.org.somemodid.item.ItemSpecialSword",
            "  enum (no arguments)",
            "Where ",
            "- class will search the given class in the inheritance chain of the item class of the current item",
            "- regex uses regular expressions and has to match the full modid:itemid",
            "- enum uses an EnumEnchantmentType that another mod has created. This matchers name needs to match that enum name",
            "- and items is an arbitrarily long list"
    })
    @Config.Name("Custom Item Types")
    public String[] customTypes = {};

    @Config.Comment("Some modded items only pretend to be a specific item type without actually being them (wolf armor being SWORD and ARMOR_FEET, better survival items being SWORD etc).\n" +
            "Enabling this toggle will make the given item type match those too.\n" +
            "Disabling will instead only look at the actual checks of each EnumEnchantmentType (like SWORD = has to be instanceof ItemSword) to match.\n" +
            "It is closer to an unmodified experience if you keep this enabled. Disable it if you want full control over what enchants go on which items using custom types.")
    @Config.Name("Allow Custom Items")
    public boolean allowCustomItems = true;

    @Config.Comment({
            "(Custom) Item Type name and a list of enchantments that can go on items with this type",
            " You can also invert item type matches by adding a \"!\" in front of the type name (first character)",
            " This will disallow any item that matches the given matcher from using the given enchantments"
    })
    @Config.Name("Item Types")
    public String[] itemTypes = {};

    @Config.Comment({
            "(Custom) Item Type name and a list of enchantments that can go on items with this type, specifically via the anvil",
            " Any predefined item type in \"Item Types\" can be used here too, including inverted ones."
    })
    @Config.Name("Item Types Anvil")
    public String[] itemTypesAnvil = {};
}
