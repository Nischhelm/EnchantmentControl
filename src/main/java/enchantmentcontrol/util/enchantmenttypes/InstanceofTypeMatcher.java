package enchantmentcontrol.util.enchantmenttypes;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;

import java.util.ArrayList;
import java.util.List;

public class InstanceofTypeMatcher implements ITypeMatcher {
    private final String name;
    private final List<Class<? extends Item>> classes = new ArrayList<>();
    private final Item fakeItem;

    @SuppressWarnings("unchecked")
    public InstanceofTypeMatcher(String name, List<String> classes) {
        for(String className : classes) {
            try { this.classes.add((Class<? extends Item>) Launch.classLoader.findClass(className));
            } catch (Exception ignored) {}
        }

        this.name = name;
        this.fakeItem = null;
    }

    public InstanceofTypeMatcher(String name, Class<? extends Item> clazz, Item fakeItem) {
        this.name = name;
        this.classes.add(clazz);
        this.fakeItem = fakeItem;
    }

    public boolean isValid() {
        return !this.name.isEmpty() && !this.classes.isEmpty();
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean matches(Enchantment enchantment, ItemStack stack, Item item, String itemName) {
        return this.classes.stream().anyMatch(cls -> cls.isAssignableFrom(item.getClass()));
    }

    @Override
    public ItemStack getFakeStack() {
        if(this.fakeItem == null) return null;
        return new ItemStack(this.fakeItem);
    }
}
