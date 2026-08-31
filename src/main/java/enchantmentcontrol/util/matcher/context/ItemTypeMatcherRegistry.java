package enchantmentcontrol.util.matcher.context;

import enchantmentcontrol.util.matcher.IMatcher;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class ItemTypeMatcherRegistry {
    private final String name;
    private final IMatcher<ItemTypeContext> matcher;
    private final Supplier<Boolean> isValid;
    private final ItemStack fakeStack;

    public ItemTypeMatcherRegistry(
        String name,
        IMatcher<ItemTypeContext> matcher,
        Supplier<Boolean> isValid,
        ItemStack fakeStack
    ) {
        this.name = name;
        this.matcher = matcher;
        this.isValid = isValid;
        this.fakeStack = fakeStack;
    }

    public String getName() {
        return name;
    }

    public IMatcher<ItemTypeContext> getMatcher() {
        return matcher;
    }

    public boolean isValid() {
        return isValid.get();
    }

    public ItemStack getFakeStack() {
        return fakeStack;
    }
}
