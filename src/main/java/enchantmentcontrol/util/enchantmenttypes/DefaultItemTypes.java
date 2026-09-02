package enchantmentcontrol.util.matchers;

import enchantmentcontrol.util.enchantmenttypes.ItemTypeMatcher;
import enchantmentcontrol.util.matchers.context.ItemTypeContext;
import enchantmentcontrol.util.matchers.matcher.BooleanMatcher;
import net.minecraft.init.Items;
import net.minecraft.item.*;

import java.util.EnumMap;

public class DefaultItemTypes {

    private static final EnumMap<DefaultType, ItemTypeMatcher> registeredDefaultMatchers = new EnumMap<>(DefaultType.class);

    public static ItemTypeMatcher get(DefaultType type) {
        return registeredDefaultMatchers.get(type);
    }

    public enum DefaultType {ANY, NONE, AXE, PICKAXE, HOE, SHOVEL, SHIELD, SHEARS}
    static {
        createDefaultMatcher(DefaultType.ANY, new BooleanMatcher<>(true), null);
        createDefaultMatcher(DefaultType.NONE, new BooleanMatcher<>(false), null);
        createDefaultMatcher(DefaultType.AXE, MatcherCreator.ITEM_TYPE.createClassMatcher(ItemAxe.class), Items.IRON_AXE);
        createDefaultMatcher(DefaultType.PICKAXE, MatcherCreator.ITEM_TYPE.createClassMatcher(ItemPickaxe.class), Items.IRON_PICKAXE);
        createDefaultMatcher(DefaultType.HOE, MatcherCreator.ITEM_TYPE.createClassMatcher(ItemHoe.class), Items.IRON_HOE);
        createDefaultMatcher(DefaultType.SHOVEL, MatcherCreator.ITEM_TYPE.createClassMatcher(ItemSpade.class), Items.IRON_SHOVEL);
        createDefaultMatcher(DefaultType.SHIELD, MatcherCreator.ITEM_TYPE.createClassMatcher(ItemShield.class), Items.SHIELD);
        createDefaultMatcher(DefaultType.SHEARS, MatcherCreator.ITEM_TYPE.createClassMatcher(ItemShears.class), Items.SHEARS);
    }

    private static void createDefaultMatcher(DefaultType type, IMatcher<ItemTypeContext> innerMatcher, Item item) {
        ItemTypeMatcher matcher = new ItemTypeMatcher(type.name(), innerMatcher, item != null ? new ItemStack(item) : null);
        registeredDefaultMatchers.put(type, matcher);
    }
}
