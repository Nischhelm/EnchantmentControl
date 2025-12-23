package enchantmentcontrol.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.provider.ItemTypeConfigProvider;
import enchantmentcontrol.util.enchantmenttypes.ITypeMatcher;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.function.Predicate;

@ZenRegister
@ZenClass("mods."+ EnchantmentControl.MODID+".CustomTypes")
@SuppressWarnings("unused")
public class CT_CustomType {
    @ZenMethod
    public static void registerCustomType(String name, Predicate<ItemStack> matcher){
        ItemTypeConfigProvider.registerCustomTypeMatcher(new ITypeMatcher() {
            @Override
            public boolean matches(Enchantment enchantment, ItemStack stack, Item item, String itemName) {
                return matcher.test(stack);
            }

            @Override
            public String getName() {
                return name;
            }
        });
    }

    @ZenMethod
    public static void registerCustomTypeWithMetadata(String name, String itemid, int metadata){
        ItemTypeConfigProvider.registerCustomTypeMatcher(new ITypeMatcher.UsesItemLoc() {
            @Override
            public boolean matches(Enchantment enchantment, ItemStack stack, Item item, String itemName) {
                return item.getRegistryName().toString().equals(itemid) && stack.getMetadata() == metadata;
            }

            @Override
            public String getName() {
                return name;
            }
        });
    }
}
