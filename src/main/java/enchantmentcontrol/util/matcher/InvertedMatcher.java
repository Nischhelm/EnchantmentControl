package enchantmentcontrol.util.matcher;

public class InvertedMatcher<T> implements IMatcher<T> {
    private final IMatcher<T> inner;

    public InvertedMatcher(IMatcher<T> inner) {
        this.inner = inner;
    }

    @Override
    public boolean matches(T context) {
        return this.inner.matches(context);
    }

    public IMatcher<T> getInner() {
        return inner;
    }
}
