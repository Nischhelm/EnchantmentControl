package enchantmentcontrol.util.matcher;

import java.util.Set;
import java.util.function.Function;

public class RegexMatcher<T> implements IMatcher<T> {
    protected final Set<String> regexes;
    private final Function<T, String> valueExtractor;

    public RegexMatcher(Set<String> regexes, Function<T, String> valueExtractor) {
        this.regexes = regexes;
        this.valueExtractor = valueExtractor;
    }

    @Override
    public boolean matches(T context) {
        String value = valueExtractor.apply(context);
        return value != null && regexes.stream().anyMatch(value::matches);
    }
}
