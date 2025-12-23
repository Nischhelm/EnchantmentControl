package enchantmentcontrol.util.enchantmenttypes;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface ITypeMatcher {
    boolean matches(Enchantment enchantment, ItemStack stack, Item item, String itemName);
    default boolean isValid(){ return true;}
    default ItemStack getFakeStack(){return null;}
    String getName();

    interface UsesItemLoc extends ITypeMatcher{} //tells the matching process to calc loc.toString
}
