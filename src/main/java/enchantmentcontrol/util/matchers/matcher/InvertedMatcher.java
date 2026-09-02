package enchantmentcontrol.util.matchers.matcher;

import enchantmentcontrol.util.matchers.IMatcher;

public class InvertedMatcher<CTX> implements IMatcher<CTX> {
    private final IMatcher<CTX> inner;

    public InvertedMatcher(IMatcher<CTX> inner) {
        this.inner = inner;
    }

    @Override
    public boolean matches(CTX context) {
        return this.inner.matches(context);
    }

    public IMatcher<CTX> getInner() {
        return inner;
    }
}
