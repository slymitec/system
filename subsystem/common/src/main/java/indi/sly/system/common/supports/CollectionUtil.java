package indi.sly.system.common.supports;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class CollectionUtil {
    public static <T> List<T> unmodifiable(List<? extends T> list) {
        return Collections.unmodifiableList(list);
    }

    public static <K, V> Map<K, V> unmodifiable(Map<? extends K, ? extends V> map) {
        return Collections.unmodifiableMap(map);
    }

    public static <T> Set<T> unmodifiable(Set<? extends T> set) {
        return Collections.unmodifiableSet(set);
    }
}
