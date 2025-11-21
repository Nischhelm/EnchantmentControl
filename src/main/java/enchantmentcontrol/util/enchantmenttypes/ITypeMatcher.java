package enchantmentcontrol.util.enchantmenttypes;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface ITypeMatcher {
    boolean matches(Enchantment enchantment, ItemStack stack, Item item, String itemName);
}
