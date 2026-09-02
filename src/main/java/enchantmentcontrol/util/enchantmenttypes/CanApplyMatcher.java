package enchantmentcontrol.util.enchantmenttypes;

import enchantmentcontrol.util.matcher.IMatcher;
import enchantmentcontrol.util.matcher.context.ItemTypeContext;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class CanApplyMatcher {
    private final String name;
    @Nullable
    private final ItemStack fakeStack;
    private final IMatcher<ItemTypeContext> matcher;

    public CanApplyMatcher(String name, IMatcher<ItemTypeContext> matcher) {
        this(name, matcher, null);
    }

    public CanApplyMatcher(String name, IMatcher<ItemTypeContext> matcher, @Nullable ItemStack fakeStack) {
        this.name = name;
        this.matcher = matcher;
        this.fakeStack = fakeStack;
    }

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
