package enchantmentcontrol.util.enchantmenttypes;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InvertedTypeMatcher implements ITypeMatcher.UsesItemLoc {
    private static final String name = "inverted";

    private final ITypeMatcher inner;

    public InvertedTypeMatcher(ITypeMatcher inner) {
        this.inner = inner;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean matches(Enchantment enchantment, ItemStack stack, Item item, String itemName) {
        return !this.inner.matches(enchantment, stack, item, itemName);
    }
}
