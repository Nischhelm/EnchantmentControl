package enchantmentcontrol.util.matchers.matcher;

import enchantmentcontrol.util.matchers.IMatcher;

import java.util.Set;
import java.util.function.Function;

public class ModIdMatcher<CTX> implements IMatcher<CTX> {
    protected final Set<String> modIds;
    private final Function<CTX, String> modIdExtractor;

    public ModIdMatcher(Set<String> modIds, Function<CTX, String> modIdExtractor) {
        this.modIds = modIds;
        this.modIdExtractor = modIdExtractor;
    }

    @Override
    public boolean matches(CTX context) {
        String modId = modIdExtractor.apply(context);
        return modId != null && modIds.stream().anyMatch(modId::equals);
    }
}
