package enchantmentcontrol.util.matcher;

public class BooleanMatcher<T> implements IMatcher<T> {
    private final boolean result;

    public BooleanMatcher(boolean result) {
        this.result = result;
    }

    @Override
    public boolean matches(T context) {
        return this.result;
    }
}
