package enchantmentcontrol.compat.somanyenchantments;

import enchantmentcontrol.util.matchers.MatcherCreator;
import enchantmentcontrol.util.matchers.itemtypes.DefaultItemTypes;
import enchantmentcontrol.util.matchers.itemtypes.ItemTypeMatcher;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OldSMECompat {
    public static final List<String> oldSMETypes = Arrays.asList("Combat Weapon", "Damageable", /*"Golden Apple",*/ "Combat Tool", "Combat Axe", "Tool Axe", "Tool Pickaxe", "Tool Hoe", "Combat Sword", "Tool Shovel", "Combat Shield", "Combat", "All Tools", "All", "None");

    public static List<ItemTypeMatcher> getMatchersForEnumType(EnumEnchantmentType type) {
        if (type.ordinal() > 11) { //not vanilla enum
            switch (type.name()) {
                //some SME 0.x types are just lists of types or renames of existing vanilla Enums
                case "Combat Shield": return single(DefaultItemTypes.get(DefaultItemTypes.Type.SHIELD));
                case "Tool Pickaxe": return single(DefaultItemTypes.get(DefaultItemTypes.Type.PICKAXE));
                case "Tool Hoe": return single(DefaultItemTypes.get(DefaultItemTypes.Type.HOE));
                case "Damageable": return single(DefaultItemTypes.get(DefaultItemTypes.Type.BREAKABLE));
                case "Combat Tool": return single(DefaultItemTypes.get(DefaultItemTypes.Type.TOOL));
                case "Combat Sword": return single(DefaultItemTypes.get(DefaultItemTypes.Type.SWORD));
                case "Combat Axe": return single(DefaultItemTypes.get(DefaultItemTypes.Type.AXE));
                case "None": return single(DefaultItemTypes.get(DefaultItemTypes.Type.NONE));
                case "All": return single(DefaultItemTypes.get(DefaultItemTypes.Type.ANY));
                case "Golden Apple":
                    return single(new ItemTypeMatcher("GOLD_APPLE", MatcherCreator.ITEM_TYPE.createClassMatcher(ItemAppleGold.class), new ItemStack(Items.GOLDEN_APPLE)));
                case "All Tools":
                    return Arrays.asList(DefaultItemTypes.get(DefaultItemTypes.Type.TOOL), DefaultItemTypes.get(DefaultItemTypes.Type.SWORD));
                case "Combat":
                    return Arrays.asList(DefaultItemTypes.get(DefaultItemTypes.Type.SWORD), DefaultItemTypes.get(DefaultItemTypes.Type.AXE));
                case "Combat Weapon":
                    return Arrays.asList(DefaultItemTypes.get(DefaultItemTypes.Type.BOW), DefaultItemTypes.get(DefaultItemTypes.Type.SWORD), DefaultItemTypes.get(DefaultItemTypes.Type.AXE));
            }
        }
        return Collections.emptyList();
    }

    private static List<ItemTypeMatcher> single(ItemTypeMatcher matcher) {
        return Collections.singletonList(matcher);
    }
}
