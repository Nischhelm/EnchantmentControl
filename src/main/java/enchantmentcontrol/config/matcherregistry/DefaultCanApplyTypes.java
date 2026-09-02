package enchantmentcontrol.config.matcherregistry;

import enchantmentcontrol.util.enchantmenttypes.CanApplyMatcher;
import enchantmentcontrol.util.matcher.IMatcher;
import enchantmentcontrol.util.matcher.context.ItemTypeContext;
import enchantmentcontrol.util.matcher.matcher.BooleanMatcher;
import enchantmentcontrol.util.matcher.matcher.ClassMatcher;
import net.minecraft.init.Items;
import net.minecraft.item.*;

import java.util.EnumMap;

public class DefaultCanApplyTypes {

    private static final EnumMap<Types, CanApplyMatcher> registeredMatchers = new EnumMap<>(Types.class);

    public static CanApplyMatcher getMatcher(Types type) {
        return registeredMatchers.get(type);
    }

    public enum Types {ANY, NONE, AXE, PICKAXE, HOE, SHOVEL, SHIELD, SHEARS}
    static {
        createDefaultMatcher(Types.ANY, new BooleanMatcher<>(true), null);
        createDefaultMatcher(Types.NONE, new BooleanMatcher<>(false), null);
        createDefaultMatcher(Types.AXE, createClassMatcher(ItemAxe.class), Items.IRON_AXE);
        createDefaultMatcher(Types.PICKAXE, createClassMatcher(ItemPickaxe.class), Items.IRON_PICKAXE);
        createDefaultMatcher(Types.HOE, createClassMatcher(ItemHoe.class), Items.IRON_HOE);
        createDefaultMatcher(Types.SHOVEL, createClassMatcher(ItemSpade.class), Items.IRON_SHOVEL);
        createDefaultMatcher(Types.SHIELD, createClassMatcher(ItemShield.class), Items.SHIELD);
        createDefaultMatcher(Types.SHEARS, createClassMatcher(ItemShears.class), Items.SHEARS);
    }

    private static void createDefaultMatcher(Types type, IMatcher<ItemTypeContext> innerMatcher, Item item) {
        CanApplyMatcher matcher = new CanApplyMatcher(type.name(), innerMatcher, item != null ? new ItemStack(item) : null);
        registeredMatchers.put(type, matcher);
    }

    private static ClassMatcher<ItemTypeContext> createClassMatcher(Class<? extends Item> clazz){
        return new ClassMatcher<>(clazz.getName(), context -> context.getItem().getClass());
    }
}
