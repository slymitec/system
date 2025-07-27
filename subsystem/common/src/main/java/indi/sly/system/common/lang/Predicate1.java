package indi.sly.system.common.lang;

import java.util.Objects;
import java.util.function.Predicate;

@FunctionalInterface
public interface Predicate1<T1> {
    boolean test(T1 t1);

    default Predicate1<T1> and(Predicate1<? super T1> other) {
        Objects.requireNonNull(other);
        return (t1) -> test(t1) && other.test(t1);
    }

    default Predicate1<T1> negate() {
        return (t1) -> !test(t1);
    }

    default Predicate1<T1> or(Predicate1<? super T1> other) {
        Objects.requireNonNull(other);
        return (t1) -> test(t1) || other.test(t1);
    }

    static <T1> Predicate1<T1> isEqual(Object targetRef) {
        return (null == targetRef)
                ? Objects::isNull
                : targetRef::equals;
    }

    @SuppressWarnings("unchecked")
    static <T1> Predicate1<T1> not(Predicate<? super T1> target) {
        Objects.requireNonNull(target);
        return (Predicate1<T1>) target.negate();
    }
}
