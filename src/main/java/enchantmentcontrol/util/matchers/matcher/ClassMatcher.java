package enchantmentcontrol.util.matchers.matcher;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.util.matchers.IMatcher;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class ClassMatcher<CTX> implements IMatcher<CTX> {
    private final Set<Class<?>> classes;
    private final Function<CTX, Object> instanceExtractor;

    public ClassMatcher(Set<String> classNames, Function<CTX, Object> instanceExtractor) {
        this.classes = new HashSet<>();
        this.instanceExtractor = instanceExtractor;
        classNames.forEach(this::addClass);
    }

    public ClassMatcher(Class<?> clazz, Function<CTX, Object> instanceExtractor) {
        this.classes = new HashSet<>();
        this.classes.add(clazz);
        this.instanceExtractor = instanceExtractor;
    }

    private void addClass(String className){
        try {
            this.classes.add(Class.forName(className));
        } catch (ClassNotFoundException e) {
            EnchantmentControl.LOGGER.warn("Could not find class {} for matcher", className);
        }
    }

    @Override
    public boolean matches(CTX context) {
        Object instance = instanceExtractor.apply(context);
        return instance != null && !classes.isEmpty()
            && classes.stream().anyMatch(clazz -> clazz.isInstance(instance));
    }
}
