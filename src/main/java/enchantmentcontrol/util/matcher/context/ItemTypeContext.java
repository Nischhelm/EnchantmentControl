package enchantmentcontrol.util.matcher.context;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemTypeContext {
    private final Enchantment enchantment;
    private final ItemStack stack;
    private final Item item;
    private final String itemName;

    public ItemTypeContext(Enchantment enchantment, ItemStack stack, Item item, String itemName) {
        this.enchantment = enchantment;
        this.stack = stack;
        this.item = item;
        this.itemName = itemName;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public ItemStack getStack() {
        return stack;
    }

    public Item getItem() {
        return item;
    }

    public String getItemName() {
        return itemName;
    }
}
