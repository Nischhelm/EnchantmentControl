package enchantmentcontrol.util.enchantmenttypes;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;

public class InstanceofTypeMatcher implements ITypeMatcher {
    private final String name;
    private final Class<? extends Item> clazz;
    private final Item fakeItem;

    @SuppressWarnings("unchecked")
    public InstanceofTypeMatcher(String name, String className) {
        Class<? extends Item> classtmp;
        try {
            classtmp = (Class<? extends Item>) Launch.classLoader.findClass(className);
        } catch (Exception e) {
            classtmp = null;
        }
        this.name = name;
        this.clazz = classtmp;
        this.fakeItem = null;
    }

    public InstanceofTypeMatcher(String name, Class<? extends Item> clazz, Item fakeItem) {
        this.name = name;
        this.clazz = clazz;
        this.fakeItem = fakeItem;
    }

    public boolean isValid() {
        return !this.name.isEmpty() && this.clazz != null;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean matches(Enchantment enchantment, ItemStack stack, Item item, String itemName) {
        return this.clazz.isAssignableFrom(item.getClass());
    }

    @Override
    public ItemStack getFakeStack() {
        if(this.fakeItem == null) return null;
        return new ItemStack(this.fakeItem);
    }
}
