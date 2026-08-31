package enchantmentcontrol.util.enchantmenttypes;

import enchantmentcontrol.util.matcher.IMatcher;
import enchantmentcontrol.util.matcher.context.ItemTypeContext;
import net.minecraft.item.ItemStack;

public class CanApplyMatcher {
    private final String name;
    private final ItemStack fakeStack;
    private final IMatcher<ItemTypeContext> matcher;

    public CanApplyMatcher(String name, IMatcher<ItemTypeContext> matcher) {
        this(name, matcher, null);
    }

    public CanApplyMatcher(String name, IMatcher<ItemTypeContext> matcher, ItemStack fakeStack) {
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

    public ItemStack getFakeStack(){return fakeStack;}

    public String getName() {
        return name;
    }
}
