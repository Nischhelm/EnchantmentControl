package enchantmentcontrol.config.matcherregistry;

import enchantmentcontrol.util.enchantmenttypes.CanApplyMatcher;
import enchantmentcontrol.util.matcher.IMatcher;
import enchantmentcontrol.util.matcher.context.ItemTypeContext;
import enchantmentcontrol.util.matcher.matcher.BooleanMatcher;
import enchantmentcontrol.util.matcher.matcher.ClassMatcher;
import net.minecraft.init.Items;
import net.minecraft.item.*;

public enum DefaultCanApplyTypes {
    ANY("ANY", new BooleanMatcher<>(true)),
    NONE("NONE", new BooleanMatcher<>(false)),
    AXE("AXE", createClassMatcher(ItemAxe.class), Items.IRON_AXE),
    PICKAXE("PICKAXE", createClassMatcher(ItemPickaxe.class), Items.IRON_PICKAXE),
    HOE("HOE", createClassMatcher(ItemHoe.class), Items.IRON_HOE),
    SHOVEL("SHOVEL", createClassMatcher(ItemSpade.class), Items.IRON_SHOVEL),
    SHIELD("SHIELD", createClassMatcher(ItemShield.class), Items.SHIELD),
    SHEARS("SHEARS", createClassMatcher(ItemShears.class), Items.SHEARS);

    private final CanApplyMatcher matcher;

    DefaultCanApplyTypes(String name, IMatcher<ItemTypeContext> matcher) {
        this(name, matcher, null);
    }

    DefaultCanApplyTypes(String name, IMatcher<ItemTypeContext> matcher, Item fakeItem) {
        this.matcher = new CanApplyMatcher(name,matcher, new ItemStack(fakeItem));
    }

    public String getTypeName() {
        return matcher.getName();
    }

    public CanApplyMatcher getMatcher() {
        return this.matcher;
    }

    private static ClassMatcher<ItemTypeContext> createClassMatcher(Class<? extends Item> clazz){
        return new ClassMatcher<>(clazz.getName(), context -> context.getItem().getClass());
    }
}
