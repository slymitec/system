package indi.sly.system.common.supports;

import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class ValueUtil {
    public static boolean isNullOrEmpty(@Nullable Object value) {
        if (ObjectUtil.isNull(value)) {
            return true;
        }

        if (value instanceof Optional) {
            return ((Optional<?>) value).isEmpty();
        } else if (value instanceof CharSequence) {
            return ((CharSequence) value).isEmpty();
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

    public static boolean isAnyNullOrEmpty(@Nullable final Object @Nullable [] values) {
        if (ObjectUtil.isAnyNull(values)) {
            return true;
        }

        for (Object value : values) {
            if (value instanceof Optional) {
                if (((Optional<?>) value).isEmpty()) {
                    return true;
                }
            } else if (value instanceof CharSequence) {
                if (((CharSequence) value).isEmpty()) {
                    return true;
                }
            } else if (value instanceof UUID) {
                if (value.equals(UUIDUtil.getEmpty())) {
                    return true;
                }
            } else if (value.getClass().isArray()) {
                if (Array.getLength(value) == 0) {
                    return true;
                }
            } else if (value instanceof Collection) {
                if (((Collection<?>) value).isEmpty()) {
                    return true;
                }
            } else if (value instanceof Map) {
                if (((Map<?, ?>) value).isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isAnyNullOrEmpty(@Nullable final Object value1, @Nullable final Object value2, @Nullable final Object... values) {
        if (ObjectUtil.isAnyNull(value1, value2, values)) {
            return true;
        }

        Object[] allValues = new Object[values.length + 2];
        allValues[0] = value1;
        allValues[1] = value2;
        System.arraycopy(values, 0, allValues, 2, values.length);

        for (Object value : allValues) {
            if (value instanceof Optional) {
                if (((Optional<?>) value).isEmpty()) {
                    return true;
                }
            } else if (value instanceof CharSequence) {
                if (((CharSequence) value).isEmpty()) {
                    return true;
                }
            } else if (value instanceof UUID) {
                if (value.equals(UUIDUtil.getEmpty())) {
                    return true;
                }
            } else if (value.getClass().isArray()) {
                if (Array.getLength(value) == 0) {
                    return true;
                }
            } else if (value instanceof Collection) {
                if (((Collection<?>) value).isEmpty()) {
                    return true;
                }
            } else if (value instanceof Map) {
                if (((Map<?, ?>) value).isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }
}