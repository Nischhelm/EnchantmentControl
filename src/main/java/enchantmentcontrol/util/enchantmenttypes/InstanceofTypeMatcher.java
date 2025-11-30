package enchantmentcontrol.util.enchantmenttypes;

import enchantmentcontrol.EnchantmentControl;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;

public class InstanceofTypeMatcher implements ITypeMatcher {
    private final String name;
    private final Class<? extends Item> clazz;

    @SuppressWarnings("unchecked")
    public InstanceofTypeMatcher(String in) {
        String[] split = in.split(EnchantmentControl.SEP);
        String nametmp;
        Class<? extends Item> classtmp;
        try {
            nametmp = split[0].trim();
            classtmp = (Class<? extends Item>) Launch.classLoader.findClass(split[1].trim());
        } catch (Exception e) {
            nametmp = "";
            classtmp = null;
        }
        this.name = nametmp;
        this.clazz = classtmp;
    }

    public InstanceofTypeMatcher(String name, Class<? extends Item> clazz) {
        this.name = name;
        this.clazz = clazz;
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
}
