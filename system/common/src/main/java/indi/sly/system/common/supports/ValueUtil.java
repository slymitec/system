package indi.sly.system.common.supports;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class ValueUtil {
    public static boolean isAnyNullOrEmpty(Object value) {
        if (ObjectUtil.isAnyNull(value)) {
            return true;
        }

        if (value instanceof Optional) {
            return ((Optional<?>) value).isEmpty();
        } else if (value instanceof CharSequence) {
            return ((CharSequence) value).length() == 0;
        } else if (value instanceof UUID) {
            return value.equals(UUIDUtil.EMPTY);
        } else if (value.getClass().isArray()) {
            return Array.getLength(value) == 0;
        } else if (value instanceof Collection) {
            return ((Collection<?>) value).isEmpty();
        } else if (value instanceof Map) {
            return ((Map<?, ?>) value).isEmpty();
        }

        return false;
    }

    public static boolean isAnyNullOrEmpty(Object... values) {
        if (ObjectUtil.isAnyNull(values) || values.length == 0) {
            return true;
        }

        for (Object value : values) {
            if (value instanceof Optional) {
                return ((Optional<?>) value).isEmpty();
            } else if (value instanceof CharSequence) {
                return ((CharSequence) value).length() == 0;
            } else if (value instanceof UUID) {
                return value.equals(UUIDUtil.getEmpty());
            } else if (value.getClass().isArray()) {
                return Array.getLength(value) == 0;
            } else if (value instanceof Collection) {
                return ((Collection<?>) value).isEmpty();
            } else if (value instanceof Map) {
                return ((Map<?, ?>) value).isEmpty();
            }
        }

        return false;
    }
}
