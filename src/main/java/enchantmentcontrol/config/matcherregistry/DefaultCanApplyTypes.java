package enchantmentcontrol.config.matcherregistry;

import enchantmentcontrol.util.enchantmenttypes.BooleanTypeMatcher;
import enchantmentcontrol.util.enchantmenttypes.InstanceofTypeMatcher;
import enchantmentcontrol.util.matcher.context.ItemTypeMatcherRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.*;

import java.util.function.Function;

public enum DefaultCanApplyTypes {
    ANY2("ANY",(name) -> {

    }),

    ANY("ANY", (name) -> {
        BooleanTypeMatcher matcher = new BooleanTypeMatcher(name, true);
        return new ItemTypeMatcherRegistry(
                matcher.getName(),
                ctx -> matcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
                matcher::isValid,
                matcher.getFakeStack()
        );
    }),
    NONE("NONE", (name) -> {
        BooleanTypeMatcher matcher = new BooleanTypeMatcher(name, false);
        return new ItemTypeMatcherRegistry(
                matcher.getName(),
                ctx -> matcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
                matcher::isValid,
                matcher.getFakeStack()
        );
    }),
    AXE("AXE", (name) -> {
        InstanceofTypeMatcher matcher = new InstanceofTypeMatcher(name, ItemAxe.class, Items.IRON_AXE);
        return new ItemTypeMatcherRegistry(
                matcher.getName(),
                ctx -> matcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
                matcher::isValid,
                matcher.getFakeStack()
        );
    }),
    PICKAXE("PICKAXE", (name) -> {
        InstanceofTypeMatcher matcher = new InstanceofTypeMatcher(name, ItemPickaxe.class, Items.IRON_PICKAXE);
        return new ItemTypeMatcherRegistry(
                matcher.getName(),
                ctx -> matcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
                matcher::isValid,
                matcher.getFakeStack()
        );
    }),
    HOE("HOE", (name) -> {
        InstanceofTypeMatcher matcher = new InstanceofTypeMatcher(name, ItemHoe.class, Items.IRON_HOE);
        return new ItemTypeMatcherRegistry(
                matcher.getName(),
                ctx -> matcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
                matcher::isValid,
                matcher.getFakeStack()
        );
    }),
    SHOVEL("SHOVEL", (name) -> {
        InstanceofTypeMatcher matcher = new InstanceofTypeMatcher(name, ItemSpade.class, Items.IRON_SHOVEL);
        return new ItemTypeMatcherRegistry(
                matcher.getName(),
                ctx -> matcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
                matcher::isValid,
                matcher.getFakeStack()
        );
    }),
    SHIELD("SHIELD", (name) -> {
        InstanceofTypeMatcher matcher = new InstanceofTypeMatcher(name, ItemShield.class, Items.SHIELD);
        return new ItemTypeMatcherRegistry(
                matcher.getName(),
                ctx -> matcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
                matcher::isValid,
                matcher.getFakeStack()
        );
    }),
    SHEARS("SHEARS", (name) -> {
        InstanceofTypeMatcher matcher = new InstanceofTypeMatcher(name, ItemShears.class, Items.SHEARS);
        return new ItemTypeMatcherRegistry(
                matcher.getName(),
                ctx -> matcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
                matcher::isValid,
                matcher.getFakeStack()
        );
    });

    private final String name;
    private final Function<String, ItemTypeMatcherRegistry> registryFactory;

    DefaultCanApplyTypes(String name, Function<String, ItemTypeMatcherRegistry> registryFactory) {
        this.name = name;
        this.registryFactory = registryFactory;
    }

    public String getTypeName() {
        return name;
    }

    public ItemTypeMatcherRegistry createRegistry() {
        return registryFactory.apply(name);
    }
}
