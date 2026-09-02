package enchantmentcontrol.util.matchers.matcher;

import enchantmentcontrol.util.matchers.IMatcher;

import java.util.Set;
import java.util.function.Function;

public class ExactStringMatcher<CTX> implements IMatcher<CTX> {
    protected final Set<String> ids;
    private final Function<CTX, String> valueExtractor;

    public ExactStringMatcher(Set<String> ids, Function<CTX, String> valueExtractor) {
        this.ids = ids;
        this.valueExtractor = valueExtractor;
    }

    @Override
    public boolean matches(CTX context) {
        String value = valueExtractor.apply(context);
        return value != null && ids.contains(value);
    }
}
