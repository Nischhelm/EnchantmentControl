package enchantmentcontrol.util.enchantmenttypes;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ListMatcher implements ITypeMatcher {
    protected final Set<String> ids;
    protected final String name;
    public ListMatcher(String name, Set<String> ids) {
        this.name = name;
        this.ids = ids.stream().map(String::trim).collect(Collectors.toSet());
    }

    @Override
    public boolean matches(Enchantment enchantment, ItemStack stack, Item item, String itemName) {
        return ids.contains(itemName);
    }

    @Override
    public boolean isValid(){
        return !ids.isEmpty();
    }

    @Override
    public String getName() {
        return this.name;
    }
}