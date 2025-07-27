package indi.sly.system.common.lang;

import java.util.Objects;
import java.util.function.BiPredicate;

@FunctionalInterface
public interface Predicate2<T1, T2> {
    boolean test(T1 t1, T2 t2);

    default Predicate2<T1, T2> and(BiPredicate<? super T1, ? super T2> other) {
        Objects.requireNonNull(other);
        return (T1 t1, T2 t2) -> test(t1, t2) && other.test(t1, t2);
    }

    default Predicate2<T1, T2> negate() {
        return (T1 t1, T2 t2) -> !test(t1, t2);
    }

    default BiPredicate<T1, T2> or(BiPredicate<? super T1, ? super T2> other) {
        Objects.requireNonNull(other);
        return (T1 t1, T2 t2) -> test(t1, t2) || other.test(t1, t2);
    }
}
