package enchantmentcontrol.util.matcher;

import java.util.function.Predicate;

public class IMatcher <T> implements Predicate<T> {

    public default matches()

    @Override
    public boolean test(T t) {
        return false;
    }
}
