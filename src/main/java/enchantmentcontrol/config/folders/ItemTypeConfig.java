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

    @Config.Comment({
            "Some modded items only pretend to be a specific item type without actually being them (wolf armor being SWORD and ARMOR_FEET, better survival items being SWORD etc).",
            "Enabling this toggle will make the given item type match those too.",
            "Disabling will instead only look at the actual checks of each EnumEnchantmentType (like SWORD = has to be instanceof ItemSword) to match.",
            "It is closer to an unmodified experience if you keep this enabled. Disable it if you want full control over what enchants go on which items using custom types."
    })
    @Config.Name("Allow Modded Item Behaviors")
    public boolean allowCustomItems = true;

    @Config.Comment("Whenever enchantments are checked against items to possibly apply the enchantment, the rules in here are checked, to a modifiably varying degree.")
    @Config.Name("General")
    public GeneralTypeConfig general = new GeneralTypeConfig();
    
    public static class GeneralTypeConfig {
        @Config.Comment({
                "Some modded enchantments come with additional item applicability rules (example: SME Rune Revival on all BREAKABLE except ARMOR). ",
                "For such enchantments, enabling this toggle will prioritise these additional rules before (and possibly instead of) checking against the types defined in \"Item Types\".",
                "Disabling will instead only use the config \"Item Types\" to match.",
                "It is closer to an unmodified experience if you keep this enabled. Disable it if you want full control over what enchants go on which items using custom types.",
                "Note: prioritising custom behavior also means that inverted types (!TYPE) won't be able to re-disallow items that were only allowed by such custom behavior"
        })
        @Config.Name("Allow Modded Enchantment Behaviors")
        public boolean allowCustomEnchantments = true;

        @Config.Comment({
                "(Custom) Item Type name and a list of enchantments that can go on items with this type",
                " Pattern: TYPE = modid:enchid, modid:enchid2, ...",
                " You can also invert item type matches by adding a \"!\" in front of the type name (first character)",
                " This will disallow any item that matches the given matcher from using the given enchantments (except if allowed custom behavior gets priority and overrides)"
        })
        @Config.Name("Item Types")
        public String[] itemTypes = {};

        @Config.Comment({
                "Enchantments listed here will run against what is set in \"Allow Modded Enchantment Behaviors\":",
                " - If modded behaviors are generally allowed, enchants listed here will not have their modded behavior allowed",
                " - If modded behaviors are generally disallowed, enchants listed here will have their modded behavior be allowed anyway",
                "" //TODO: auto fill blacklist with general disable
        })
        @Config.Name("Blacklist")
        public String[] blacklist = {};
    }

    @Config.Comment({
            "This is a vanilla override thats called when an enchantment is applied to an item using ",
            " - the anvil",
            " - or the /enchant command",
            "(java internal: canApply)",
            "The behavior in here is usually added on top of the general behavior, if modded enchantments don't change that up (and they are allowed to)"
    })
    @Config.Name("Anvil")
    public AnvilTypeConfig anvil = new AnvilTypeConfig();
    
    public static class AnvilTypeConfig {
        @Config.Comment({
                "Same as \"General.Allow Modded Enchantment Behaviors\" but specifically for anvil and /enchant command.",
                "Overriding the canApply method is pretty common for mods, so allowing this is a good idea..",
                " Even vanilla does it, to",
                " - allow Sharpness, Smite & BoA on Axes",
                " - and to allow Thorns on all armor pieces, not just Chestplate",
                "Disable this only if you want total control, then only the \"Anvil.Item Types\" are used, on top of whatever behavior happens in General"
        })
        @Config.Name("Allow Modded Enchantment Behaviors")
        public boolean allowCustomEnchantments = true;

        @Config.Comment({
                "(Custom) Item Type name and a list of enchantments that can go on items with this type when using the anvil (and the /enchant command), additionally to the \"General.Item Types\"",
                " Any type name from \"General.Item Types\" and \"Custom Item Types\" can be used here too, including inverted(!) ones."
        })
        @Config.Name("Item Types")
        public String[] itemTypes = {};

        @Config.Comment({
                "Enchantments listed here will run against what is set in \"Allow Modded Enchantment Behaviors\":",
                " - If modded behaviors are generally allowed, enchants listed here will not have their modded behavior allowed",
                " - If modded behaviors are generally disallowed, enchants listed here will have their modded behavior be allowed anyway",
                "" //TODO: auto fill blacklist
        })
        @Config.Name("Blacklist")
        public String[] blacklist = {};
    }
}
