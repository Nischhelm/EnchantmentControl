package enchantmentcontrol.util.matcher;

import java.util.Set;
import java.util.function.Function;

public class ListMatcher<T> implements IMatcher<T> {
    protected final Set<String> ids;
    private final Function<T, String> valueExtractor;

    public ListMatcher(Set<String> ids, Function<T, String> valueExtractor) {
        this.ids = ids;
        this.valueExtractor = valueExtractor;
    }

    @Override
    public boolean matches(T context) {
        String value = valueExtractor.apply(context);
        return value != null && ids.contains(value);
    }
}
