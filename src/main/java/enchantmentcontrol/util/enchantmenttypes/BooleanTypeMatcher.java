package enchantmentcontrol.util.enchantmenttypes;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BooleanTypeMatcher implements ITypeMatcher {
    private final String name;
    private final boolean result;

    @SuppressWarnings("unchecked")
    public BooleanTypeMatcher(String in, boolean result) {
        this.name = in;
        this.result = result;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean matches(Enchantment enchantment, ItemStack stack, Item item, String itemName) {
        return this.result;
    }
}
