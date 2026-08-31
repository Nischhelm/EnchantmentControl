package enchantmentcontrol.util.enchantmenttypes;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Set;

public class CustomTypeMatcher implements ITypeMatcher.UsesItemLoc {
    private final String name;
    private final Set<String> regexes;

    public CustomTypeMatcher(String name, Set<String> regexes) {
        this.name = name;
        this.regexes = regexes;
    }

    public boolean isValid() {
        return !this.name.isEmpty() && !this.regexes.isEmpty();
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean matches(Enchantment enchantment, ItemStack stack, Item item, String itemName) {
        return this.regexes.stream().anyMatch(itemName::matches);
    }
}
