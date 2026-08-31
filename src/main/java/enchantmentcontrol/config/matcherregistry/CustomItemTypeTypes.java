package enchantmentcontrol.config.matcherregistry;

import enchantmentcontrol.util.enchantmenttypes.InstanceofTypeMatcher;
import enchantmentcontrol.util.matcher.context.ItemTypeContext;
import enchantmentcontrol.util.matcher.context.ItemTypeMatcherRegistry;
import enchantmentcontrol.util.matcher.matcher.ModIdMatcher;
import enchantmentcontrol.util.matcher.matcher.RegexMatcher;
import enchantmentcontrol.util.matcher.matcher.StringListMatcher;
import net.minecraftforge.fml.common.Loader;

import java.util.Set;
import java.util.stream.Collectors;

public enum CustomItemTypeTypes {
    MODID {
        @Override
        public ItemTypeMatcherRegistry createRegistry(String name, Set<String> values) {
            return new ItemTypeMatcherRegistry(
                    name,
                    new ModIdMatcher<>(values, ctx -> {
                        if (ctx.getItem().getRegistryName() == null) return null;
                        return ctx.getItem().getRegistryName().getNamespace();
                    }),
                    () -> values.stream().anyMatch(Loader::isModLoaded),
                    null
            );
        }
    },
    REGEX {
        @Override
        public ItemTypeMatcherRegistry createRegistry(String name, Set<String> values) {
            return new ItemTypeMatcherRegistry(
                    name,
                    new RegexMatcher<>(values, ItemTypeContext::getItemName),
                    () -> !values.isEmpty(),
                    null
            );
        }
    },
    ITEMID {
        @Override
        public ItemTypeMatcherRegistry createRegistry(String name, Set<String> values) {
            Set<String> trimmed = values.stream().map(String::trim).collect(Collectors.toSet());
            return new ItemTypeMatcherRegistry(
                    name,
                    new StringListMatcher<>(trimmed, ItemTypeContext::getItemName),
                    () -> !trimmed.isEmpty(),
                    null
            );
        }
    },
    CLASS {
        @Override
        public ItemTypeMatcherRegistry createRegistry(String name, Set<String> values) {
            InstanceofTypeMatcher classMatcher =
                    new InstanceofTypeMatcher(
                            name,
                            values.stream().map(String::trim).collect(Collectors.toList())
                    );
            return new ItemTypeMatcherRegistry(
                    classMatcher.getName(),
                    ctx -> classMatcher.matches(ctx.getEnchantment(), ctx.getStack(), ctx.getItem(), ctx.getItemName()),
                    classMatcher::isValid,
                    classMatcher.getFakeStack()
            );
        }
    };

    public abstract ItemTypeMatcherRegistry createRegistry(String name, Set<String> values);
}
