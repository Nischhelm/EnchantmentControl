package enchantmentcontrol.util.enchantmenttypes;

import enchantmentcontrol.config.provider.ItemTypeConfigProvider;
import enchantmentcontrol.util.matchers.MatcherCreator;
import enchantmentcontrol.util.matchers.context.ItemTypeContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;

import java.util.*;

public class EnumEnchantmentTypeMatcher extends ItemTypeMatcher {
    private final EnumEnchantmentType type;

    public EnumEnchantmentTypeMatcher(String name, EnumEnchantmentType type, Item item) {
        super(name, null, item == null ? null : new ItemStack(item));
        this.type = type;
    }

    @Override
    public boolean matches(ItemTypeContext context) {
        Enchantment ench = context.getEnchantment();
        Item item = context.getItem();

        // This tries to catch all items that pretend to be normal MC items without inheriting from them
        // which then try to get the correct enchantments by overriding item.canApplyAtEnchantingTable(enchantment) using
        // enchantment.type == myPretended_vanillaEnumEnchantment_type

        // The main issue why we cant use the normal system is that vanilla only allows one type per enchant
        if (ItemTypeConfigProvider.shouldYieldToModdedBehavior(item)) {
            EnumEnchantmentType tmpType = ench.type;
            ench.type = this.type;
            boolean doesMatch = item.canApplyAtEnchantingTable(context.getStack(), ench);
            ench.type = tmpType;

            return doesMatch;
        } else return type.canEnchantItem(item);
    }
}
