package enchantmentcontrol.util.enchantmenttypes;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public class ModidMatcher implements ITypeMatcher {
    private final String name;
    private final String modid;

    public ModidMatcher(String name, String modid) {
        this.name = name;
        this.modid = modid;
    }

    public boolean isValid() {
        return Loader.isModLoaded( this.modid);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean matches(Enchantment enchantment, ItemStack stack, Item item, String itemName) {
        return item.getRegistryName() != null && item.getRegistryName().getNamespace().equals(this.modid);
    }
}
