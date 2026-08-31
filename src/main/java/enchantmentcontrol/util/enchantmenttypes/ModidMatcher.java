package enchantmentcontrol.util.enchantmenttypes;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import java.util.Set;

public class ModidMatcher implements ITypeMatcher {
    private final String name;
    private final Set<String> modids;

    public ModidMatcher(String name, Set<String> modids) {
        this.name = name;
        this.modids = modids;
    }

    public boolean isValid() {
        return this.modids.stream().anyMatch(Loader::isModLoaded);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean matches(Enchantment enchantment, ItemStack stack, Item item, String itemName) {
        if (item.getRegistryName() == null) return false;
        String modid = item.getRegistryName().getNamespace();
        return this.modids.stream().anyMatch(modid::equals);
    }
}
