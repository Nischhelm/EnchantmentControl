package enchantmentcontrol.util.matchers;

import enchantmentcontrol.util.matchers.context.EntityMatcherContext;
import enchantmentcontrol.util.matchers.context.IContext;
import enchantmentcontrol.util.matchers.context.ItemTypeContext;
import enchantmentcontrol.util.matchers.matcher.*;

import java.util.Collections;
import java.util.Set;

public class MatcherCreator<T extends IContext> {

    public static final MatcherCreator<ItemTypeContext> ITEM_TYPE = new MatcherCreator<>();
    public static final MatcherCreator<EntityMatcherContext> ENTITY = new MatcherCreator<>();

    public IMatcher<T> createMatcher(Set<String> values, EnumMatcherType matcherType){
        switch (matcherType){
            case MODID:
                return new ModIdMatcher<>(values, ctx -> ctx.getLocation().getNamespace());
            case CLASS:
                return new ClassMatcher<>(values, IContext::getObject);
            case REGEX:
                return new RegexMatcher<>(values, ctx -> ctx.getLocation().toString());
            case EXACT: default:
                return new ExactStringMatcher<T>(values, ctx -> ctx.getLocation().toString());
        }
    }

    public IMatcher<T> createClassMatcher(Class<?> clazz){
        return new ClassMatcher<>(clazz, IContext::getObject);
    }

    public IMatcher<T> createRegexMatcher(String values){
        return createMatcher(Collections.singleton(values), EnumMatcherType.REGEX);
    }

    public IMatcher<T> createInvertedMatcher(IMatcher<T> matcher){
        return new InvertedMatcher<>(matcher);
    }
}
