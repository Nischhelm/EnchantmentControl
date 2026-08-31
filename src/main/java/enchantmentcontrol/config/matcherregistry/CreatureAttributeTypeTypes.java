package enchantmentcontrol.config.matcherregistry;

import enchantmentcontrol.util.matcher.IMatcher;
import enchantmentcontrol.util.matcher.context.EntityMatcherContext;
import enchantmentcontrol.util.matcher.matcher.ClassMatcher;
import enchantmentcontrol.util.matcher.matcher.ModIdMatcher;
import enchantmentcontrol.util.matcher.matcher.RegexMatcher;
import enchantmentcontrol.util.matcher.matcher.StringListMatcher;

import java.util.Set;
import java.util.function.Function;

public enum CreatureAttributeTypeTypes {
    MOB(values -> new StringListMatcher<>(values, ctx -> ctx.getLocation().toString())),
    MODID(values -> new ModIdMatcher<>(values, ctx -> ctx.getLocation().getNamespace())),
    CLASS(values -> new ClassMatcher<>(values, EntityMatcherContext::getEntity)),
    REGEX(values -> new RegexMatcher<>(values, ctx -> ctx.getLocation().toString()));

    private final Function<Set<String>, IMatcher<EntityMatcherContext>> matcherFactory;

    CreatureAttributeTypeTypes(Function<Set<String>, IMatcher<EntityMatcherContext>> matcherFactory) {
        this.matcherFactory = matcherFactory;
    }

    public IMatcher<EntityMatcherContext> createMatcher(Set<String> values) {
        return matcherFactory.apply(values);
    }
}
