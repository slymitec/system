package indi.sly.system.common.supports;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import indi.sly.system.common.lang.*;
import org.apache.fory.Fory;
import org.apache.fory.ThreadSafeFory;
import org.apache.fory.config.Language;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class ObjectUtil {
    private static final String TO_STRING_NULL_OBJECT = "null";
    private static final ObjectMapper SERIALIZATION_JSON = new ObjectMapper();
    private static final ThreadSafeFory SERIALIZATION_BINARY = Fory.builder().withLanguage(Language.JAVA).withAsyncCompilation(true).requireClassRegistration(false).buildThreadSafeFory();

    public static boolean isNull(final Object value) {
        return ObjectUtil.isAnyNull(value);
    }

    public static boolean isAnyNull(final Object value) {
        if (value == null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isAnyNull(final Object... values) {
        if (values == null || values.length == 0) {
            return true;
        } else {
            for (Object value : values) {
                if (value == null) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean notNull(final Object value) {
        if (value == null) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean allNotNull(final Object value) {
        if (value == null) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean allNotNull(final Object... values) {
        if (values == null || values.length == 0) {
            return false;
        } else {
            for (Object value : values) {
                if (value == null) {
                    return false;
                }
            }
            return true;
        }
    }

    public static Object unwrapOptional(Object value) {
        if (value instanceof Optional<?> optional) {
            if (optional.isEmpty()) {
                return null;
            }
            Object result = optional.get();
            return ObjectUtil.unwrapOptional(result);
        }
        return value;
    }

    public static boolean equals(Object value1, Object value2) {
        if (value1 == value2) {
            return true;
        }
        if (value1 == null || value2 == null) {
            return false;
        }
        if (value1.equals(value2)) {
            return true;
        }
        if (value1.getClass().isArray() && value2.getClass().isArray()) {
            switch (value1) {
                case Object[] objects when value2 instanceof Object[] -> {
                    return ArrayUtil.equals(objects, (Object[]) value2);
                }
                case boolean[] booleans when value2 instanceof boolean[] -> {
                    return ArrayUtil.equals(booleans, (boolean[]) value2);
                }
                case byte[] bytes when value2 instanceof byte[] -> {
                    return ArrayUtil.equals(bytes, (byte[]) value2);
                }
                case char[] chars when value2 instanceof char[] -> {
                    return ArrayUtil.equals(chars, (char[]) value2);
                }
                case double[] doubles when value2 instanceof double[] -> {
                    return ArrayUtil.equals(doubles, (double[]) value2);
                }
                case float[] floats when value2 instanceof float[] -> {
                    return ArrayUtil.equals(floats, (float[]) value2);
                }
                case int[] ints when value2 instanceof int[] -> {
                    return ArrayUtil.equals(ints, (int[]) value2);
                }
                case long[] longs when value2 instanceof long[] -> {
                    return ArrayUtil.equals(longs, (long[]) value2);
                }
                case short[] shorts when value2 instanceof short[] -> {
                    return ArrayUtil.equals(shorts, (short[]) value2);
                }
                default -> {
                }
            }
        }

        return false;
    }

    public static int hashCode(Object value) {
        if (ObjectUtil.isAnyNull(value)) {
            return 0;
        }

        if (value.getClass().isArray()) {
            switch (value) {
                case Object[] objects -> {
                    return ArrayUtil.hashCode(objects);
                }
                case boolean[] booleans -> {
                    return ArrayUtil.hashCode(booleans);
                }
                case byte[] bytes -> {
                    return ArrayUtil.hashCode(bytes);
                }
                case char[] chars -> {
                    return ArrayUtil.hashCode(chars);
                }
                case double[] doubles -> {
                    return ArrayUtil.hashCode(doubles);
                }
                case float[] floats -> {
                    return ArrayUtil.hashCode(floats);
                }
                case int[] ints -> {
                    return ArrayUtil.hashCode(ints);
                }
                case long[] longs -> {
                    return ArrayUtil.hashCode(longs);
                }
                case short[] shorts -> {
                    return ArrayUtil.hashCode(shorts);
                }
                default -> {
                }
            }
        }

        return value.hashCode();
    }

    public static String toString(Object value) {
        if (ObjectUtil.isAnyNull(value)) {
            return ObjectUtil.TO_STRING_NULL_OBJECT;
        }
        return value.toString();
    }

    public static byte[] transferToByteArray(Object value) {
        return ObjectUtil.SERIALIZATION_BINARY.serialize(value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T transferFromByteArray(byte[] stream) {
        return (T) ObjectUtil.SERIALIZATION_BINARY.deserialize(stream);
    }

    public static String transferToString(Object value) {
        try {
            return ObjectUtil.SERIALIZATION_JSON.writeValueAsString(value);
        } catch (JsonProcessingException ignored) {
            throw new StatusUnexpectedException();
        }
    }

    public static <T> T transferFromString(Class<T> clazz, String value) {
        return ObjectUtil.transferFromStringOrDefaultProvider(clazz, value, () -> null);
    }

    public static <T> T transferFromStringOrDefault(Class<T> clazz, String value, T defaultValue) {
        return ObjectUtil.transferFromStringOrDefaultProvider(clazz, value, () -> defaultValue);
    }

    public static <T> T transferFromStringOrDefaultProvider(Class<T> clazz, String value, Provider<T> defaultProvider) {
        if (ObjectUtil.isAnyNull(clazz, defaultProvider)) {
            throw new ConditionParametersException();
        }

        try {
            return ObjectUtil.SERIALIZATION_JSON.readValue(value, clazz);
        } catch (Exception ignored) {
            return defaultProvider.acquire();
        }
    }

    public static <T> List<T> transferListFromString(Class<T> clazz, String value) {
        return ObjectUtil.transferListFromStringOrDefaultProvider(clazz, value, () -> null);
    }

    public static <T> List<T> transferListFromStringOrDefault(Class<T> clazz, String value, List<T> defaultValue) {
        return ObjectUtil.transferListFromStringOrDefaultProvider(clazz, value, () -> defaultValue);
    }

    public static <T> List<T> transferListFromStringOrDefaultProvider(Class<T> clazz, String value, Provider<List<T>> defaultProvider) {
        if (ObjectUtil.isAnyNull(clazz, defaultProvider)) {
            throw new ConditionParametersException();
        }

        JavaType type = ObjectUtil.SERIALIZATION_JSON.getTypeFactory().constructParametricType(List.class, clazz);

        try {
            return ObjectUtil.SERIALIZATION_JSON.readValue(value, type);
        } catch (Exception ignored) {
            return defaultProvider.acquire();
        }
    }

    public static <TK, TV> Map<TK, TV> transferMapFromString(Class<TK> keyClass, Class<TV> valueClass, String value) {
        return ObjectUtil.transferMapFromStringOrDefaultProvider(keyClass, valueClass, value, () -> null);
    }

    public static <TK, TV> Map<TK, TV> transferMapFromStringOrDefault(Class<TK> keyClass, Class<TV> valueClass, String value, Map<TK, TV> defaultValue) {
        return ObjectUtil.transferMapFromStringOrDefaultProvider(keyClass, valueClass, value, () -> defaultValue);
    }

    public static <TK, TV> Map<TK, TV> transferMapFromStringOrDefaultProvider(Class<TK> keyClass, Class<TV> valueClass, String value, Provider<Map<TK, TV>> defaultProvider) {
        if (ObjectUtil.isAnyNull(keyClass, valueClass, defaultProvider)) {
            throw new ConditionParametersException();
        }

        JavaType type = ObjectUtil.SERIALIZATION_JSON.getTypeFactory().constructParametricType(Map.class, keyClass, valueClass);

        try {
            return ObjectUtil.SERIALIZATION_JSON.readValue(value, type);
        } catch (Exception ignored) {
            return defaultProvider.acquire();
        }
    }
}