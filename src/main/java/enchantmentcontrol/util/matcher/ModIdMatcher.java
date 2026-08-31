package enchantmentcontrol.util.matcher;

import java.util.Set;
import java.util.function.Function;

public class ModIdMatcher<T> implements IMatcher<T> {
    protected final Set<String> modIds;
    private final Function<T, String> modIdExtractor;

    public ModIdMatcher(Set<String> modIds, Function<T, String> modIdExtractor) {
        this.modIds = modIds;
        this.modIdExtractor = modIdExtractor;
    }

    @Override
    public boolean matches(T context) {
        String modId = modIdExtractor.apply(context);
        return modId != null && modIds.stream().anyMatch(modId::equals);
    }
}
