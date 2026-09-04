package enchantmentcontrol.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import enchantmentcontrol.Tags;
import enchantmentcontrol.config.provider.ItemTypeConfigProvider;
import enchantmentcontrol.util.matchers.context.ItemTypeContext;
import enchantmentcontrol.util.matchers.itemtypes.ItemTypeMatcher;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.function.Predicate;

@ZenRegister
@ZenClass("mods."+ Tags.MODID+".CustomTypes")
@SuppressWarnings("unused")
public class CT_CustomType {
    @ZenMethod
    public static void registerCustomType(String name, Predicate<ItemStack> matcher){
        ItemTypeConfigProvider.registerCustomTypeMatcher(new ItemTypeMatcher(name, null) {
            @Override
            public boolean matches(ItemTypeContext context) {
                return matcher.test(context.getStack());
            }
        });
    }

    @ZenMethod
    public static void registerCustomTypeWithMetadata(String name, String itemid, int metadata){
        ItemTypeConfigProvider.registerCustomTypeMatcher(new ItemTypeMatcher(name, null) {
            @Override
            public boolean matches(ItemTypeContext context) {
                return context.getItemName().equals(itemid) && context.getMetadata() == metadata;
            }

            @Override
            public String getName() {
                return name;
            }
        });
    }
}
