package enchantmentcontrol.util.matchers.itemtypes;

import enchantmentcontrol.util.matchers.IMatcher;
import enchantmentcontrol.util.matchers.context.ItemTypeContext;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemTypeMatcher implements IMatcher<ItemTypeContext> {
    private final String name;
    @Nullable
    private final ItemStack fakeStack;
    private final IMatcher<ItemTypeContext> matcher;

    public ItemTypeMatcher(String name, IMatcher<ItemTypeContext> matcher) {
        this(name, matcher, null);
    }

    public ItemTypeMatcher(String name, IMatcher<ItemTypeContext> matcher, @Nullable ItemStack fakeStack) {
        this.name = name;
        this.matcher = matcher;
        this.fakeStack = fakeStack;
    }

    @Override
    public boolean matches(ItemTypeContext context) {
        return matcher.matches(context);
    }

    public IMatcher<ItemTypeContext> getMatcher() {
        return matcher;
    }

    @Nullable
    public ItemStack getFakeStack(){return fakeStack;}

    public String getName() {
        return name;
    }
}
