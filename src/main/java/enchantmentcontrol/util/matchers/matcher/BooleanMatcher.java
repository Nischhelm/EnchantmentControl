package enchantmentcontrol.util.matchers.matcher;

import enchantmentcontrol.util.matchers.IMatcher;

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
