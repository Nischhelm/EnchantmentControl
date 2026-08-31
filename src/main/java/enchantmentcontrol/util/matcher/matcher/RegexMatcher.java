package enchantmentcontrol.util.matcher.matcher;

import enchantmentcontrol.util.matcher.IMatcher;

import java.util.Set;
import java.util.function.Function;

public class RegexMatcher<CTX> implements IMatcher<CTX> {
    protected final Set<String> regexes;
    private final Function<CTX, String> valueExtractor;

    public RegexMatcher(Set<String> regexes, Function<CTX, String> valueExtractor) {
        this.regexes = regexes;
        this.valueExtractor = valueExtractor;
    }

    @Override
    public boolean matches(CTX context) {
        String value = valueExtractor.apply(context);
        return value != null && regexes.stream().anyMatch(value::matches);
    }
}
