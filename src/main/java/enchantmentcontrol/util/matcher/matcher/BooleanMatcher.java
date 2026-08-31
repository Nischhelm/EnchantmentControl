package enchantmentcontrol.util.matcher.matcher;

import enchantmentcontrol.util.matcher.IMatcher;

public class BooleanMatcher<CTX> implements IMatcher<CTX> {
    private final boolean result;

    public BooleanMatcher(boolean result) {
        this.result = result;
    }

    @Override
    public boolean matches(CTX context) {
        return this.result;
    }
}
