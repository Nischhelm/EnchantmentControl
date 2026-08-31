package enchantmentcontrol.config.matcherregistry;

import enchantmentcontrol.util.enchantmenttypes.CanApplyMatcher;
import enchantmentcontrol.util.matcher.context.ItemTypeContext;
import enchantmentcontrol.util.matcher.matcher.ClassMatcher;
import enchantmentcontrol.util.matcher.matcher.ModIdMatcher;
import enchantmentcontrol.util.matcher.matcher.RegexMatcher;
import enchantmentcontrol.util.matcher.matcher.StringListMatcher;

import java.util.Set;
import java.util.stream.Collectors;

public enum CustomItemTypeCreator {
    MODID {
        @Override
        public CanApplyMatcher createMatcher(String name, Set<String> values) {
            return new CanApplyMatcher(name, new ModIdMatcher<>(values, ItemTypeContext::getItemName));
        }
    },
    REGEX {
        @Override
        public CanApplyMatcher createMatcher(String name, Set<String> values) {
            return new CanApplyMatcher(name, new RegexMatcher<>(values, ItemTypeContext::getItemName));
        }
    },
    ITEMID {
        @Override
        public CanApplyMatcher createMatcher(String name, Set<String> values) {
            Set<String> trimmed = values.stream().map(String::trim).collect(Collectors.toSet());
            return new CanApplyMatcher(name, new StringListMatcher<>(trimmed, ItemTypeContext::getItemName));
        }
    },
    CLASS {
        @Override
        public CanApplyMatcher createMatcher(String name, Set<String> values) {
            return new CanApplyMatcher(name, new ClassMatcher<>(values, ItemTypeContext::getItemName));
        }
    };

    public abstract CanApplyMatcher createMatcher(String name, Set<String> values);
}
