package enchantmentcontrol.config.folders;

import enchantmentcontrol.util.matchers.EnumMatcherType;
import meldexun.betterconfig.api.Order;
import net.minecraftforge.common.config.Config;

import java.util.*;

public class ItemTypeConfig {
    @Config.Comment("Global Toggle to disable the entire Item Types category")
    @Config.Name("Section Enabled")
    @Order(0)
    public boolean enable = true;

    @Config.Comment({
            "Enchantments are allowed on items using matchers. Define custom matchers here.",
            "Pattern: MatcherName -> Type + List of values",
            "Available Types:",
            "  MODID: matches items against whole modid(s)",
            "  REGEX: matches item ids against regex(es)",
            "  ITEMID: matches item id(s) exactly",
            "  CLASS: matches items that have given class(es) in java inheritance hierarchy, write as some.org.some.modid.item.ItemSpecialSword, ...",
    })
    @Config.Name("Custom Item Types")
    @Order(1)
    public Map<String, CustomItemType> customTypes = new HashMap<>();
    public static class CustomItemType {
        public EnumMatcherType type = EnumMatcherType.EXACT;
        public Set<String> values = new HashSet<>();
        public CustomItemType(){} //needed for BetterConfig
    }

    @Config.Comment({
            "Some modded items only pretend to be a specific item type without actually being them (wolf armor being SWORD and ARMOR_FEET, better survival items being SWORD etc).",
            "Enabling this toggle will make the given item type match those too.",
            "Disabling will instead only look at the actual checks of each EnumEnchantmentType (like SWORD = has to be instanceof ItemSword) to match.",
            "It is closer to an unmodified experience if you keep this enabled. Disable it if you want full control over what enchants go on which items using custom types.",
            "DEV NOTE: this toggle decides whether CustomItem.canApplyAtEnchantingTable is checked"
    })
    @Config.Name("Allow Modded Item Behaviors")
    @Order(2)
    public boolean allowCustomItems = true;

    @Config.Comment({
            "Enchantments listed here will run against what is set in \"Allow Modded Item Behaviors\":",
            " - If modded behaviors are generally allowed, items listed here will not have their modded behavior allowed",
            " - If modded behaviors are generally disallowed, items listed here will have their modded behavior be allowed anyway",
    })
    @Config.Name("Allow Modded Item Blacklist")
    @Order(3)
    public List<String> blacklist = new ArrayList<>();

    @Config.Comment("Whenever enchantments are checked against items to possibly apply the enchantment, the rules in here are checked, to a modifiably varying degree.")
    @Config.Name("General")
    @Order(4)
    public GeneralTypeConfig general = new GeneralTypeConfig();

    @Config.Comment({
            "Allow creative mode to ignore the canApply-AtEnchTable check for items matching the enchantment in the ANVIL, the /enchant CMD, both or neither. ",
            "Vanilla default: ANVIL",
            "Set to ANVIL to disable mixin (requires restart)"
    })
    @Config.Name("(MixinToggle) Creative Skips Item Check")
    @Order(6)
    public EnumCreativeAllowed creativeOptions = EnumCreativeAllowed.NONE;
    public enum EnumCreativeAllowed { BOTH, ANVIL, CMD, NONE}
    
    public static class GeneralTypeConfig {
        @Config.Comment("Global Toggle to disable the entire General Item Types category")
        @Config.Name("Section Enabled")
        @Order(0)
        public boolean enable = true;

        @Config.Comment({
                "Some modded enchantments come with additional item applicability rules (example: SME Rune Revival on all BREAKABLE except ARMOR). ",
                "For such enchantments, enabling this toggle will prioritise these additional rules before (and possibly instead of) checking against the types defined in \"Item Types\".",
                "Disabling will instead only use the config \"Item Types\" to match.",
                "It is closer to an unmodified experience if you keep this enabled. Disable it if you want full control over what enchants go on which items using custom types.",
                "Note: prioritising custom behavior also means that inverted types (!TYPE) won't be able to re-disallow items that were only allowed by such custom behavior",
                "DEV NOTE: this toggle decides whether only CustomEnchantment.canApplyAtEnchantingTable is checked vs only against Item Types config"
        })
        @Config.Name("Allow Modded Enchantment Behaviors")
        @Order(1)
        public boolean allowCustomEnchantments = true;

        @Config.Comment({
                "Enchantments listed here will run against what is set in \"Allow Modded Enchantment Behaviors\":",
                " - If modded behaviors are generally allowed, enchants listed here will not have their modded behavior allowed",
                " - If modded behaviors are generally disallowed, enchants listed here will have their modded behavior be allowed anyway",
        })
        @Config.Name("Allow Modded Enchantment Blacklist")
        @Order(2)
        public List<String> blacklist = new ArrayList<>();

        @Config.Comment({
                "(Custom) Item Type name and a list of enchantments that can go on items with this type",
                " Pattern: type name -> list of enchantments",
                " You can also invert item type matches by adding a \"!\" in front of the type name (first character)",
                " This will disallow any item that matches the given matcher from using the given enchantments (except if allowed custom behavior gets priority and overrides)"
        })
        @Config.Name("Item Types")
        @Order(3)
        public Map<String, ArrayList<String>> itemTypes = new LinkedHashMap<>();
    }

    @Config.Comment({
            "This is a vanilla override thats called when an enchantment is applied to an item using ",
            " - the anvil",
            " - or the /enchant command",
            "(java internal: canApply)",
            "The behavior in here is usually added on top of the general behavior, if modded enchantments don't change that up (and they are allowed to)"
    })
    @Config.Name("Anvil")
    @Order(5)
    public AnvilTypeConfig anvil = new AnvilTypeConfig();
    
    public static class AnvilTypeConfig {
        @Config.Comment("Global Toggle to disable the entire Anvil-specific Item Types category")
        @Config.Name("Section Enabled")
        @Order(0)
        public boolean enable = true;

        @Config.Comment({
                "Same as \"General.Allow Modded Enchantment Behaviors\" but specifically for anvil and /enchant command.",
                "Overriding the canApply method is pretty common for mods, so allowing this is a good idea..",
                " Even vanilla does it, to",
                " - allow Sharpness, Smite & BoA on Axes",
                " - and to allow Thorns on all armor pieces, not just Chestplate",
                "Disable this only if you want total control, then only the \"Anvil.Item Types\" are used, on top of whatever behavior happens in General",
                "DEV NOTE: this toggle decides whether only CustomEnchantment.canApply is checked vs Item Types config || super.canApply"
        })
        @Config.Name("Allow Modded Enchantment Behaviors")
        @Order(1)
        public boolean allowCustomEnchantments = true;

        @Config.Comment({
                "Enchantments listed here will run against what is set in \"Allow Modded Enchantment Behaviors\":",
                " - If modded behaviors are generally allowed, enchants listed here will not have their modded behavior allowed",
                " - If modded behaviors are generally disallowed, enchants listed here will have their modded behavior be allowed anyway"
        })
        @Config.Name("Allow Modded Enchantment Blacklist")
        @Order(2)
        public List<String> blacklist = new ArrayList<>();

        @Config.Comment({
                "(Custom) Item Type name and a list of enchantments that can go on items with this type when using the anvil (and the /enchant command), additionally to the \"General.Item Types\"",
                " Any type name from \"General.Item Types\" and \"Custom Item Types\" can be used here too, including inverted(!) ones."
        })
        @Config.Name("Item Types")
        @Order(3)
        public Map<String, ArrayList<String>> itemTypes = new LinkedHashMap<>();
    }
}