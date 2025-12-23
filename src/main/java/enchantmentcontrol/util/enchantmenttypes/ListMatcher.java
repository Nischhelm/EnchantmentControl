package enchantmentcontrol.util.enchantmenttypes;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ListMatcher implements ITypeMatcher.UsesItemLoc {
    protected final Set<String> ids;
    public ListMatcher(String[] ids) {
        this.ids = new HashSet<>(Arrays.asList(Arrays.copyOfRange(ids, 2, ids.length)));
    }

    @Override
    public boolean matches(Enchantment enchantment, ItemStack stack, Item item, String itemName) {
        return ids.contains(itemName);
    }

    @Override
    public boolean isValid(){
        return !ids.isEmpty();
    }
}