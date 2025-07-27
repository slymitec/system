package indi.sly.system.common.supports;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import indi.sly.system.common.lang.*;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class ObjectUtil {
    private static final String TO_STRING_NULL_OBJECT = "null";
    private static final ObjectMapper JSON_HELPER = new ObjectMapper();

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
            if (value1 instanceof Object[] && value2 instanceof Object[]) {
                return ArrayUtil.equals((Object[]) value1, (Object[]) value2);
            }
            if (value1 instanceof boolean[] && value2 instanceof boolean[]) {
                return ArrayUtil.equals((boolean[]) value1, (boolean[]) value2);
            }
            if (value1 instanceof byte[] && value2 instanceof byte[]) {
                return ArrayUtil.equals((byte[]) value1, (byte[]) value2);
            }
            if (value1 instanceof char[] && value2 instanceof char[]) {
                return ArrayUtil.equals((char[]) value1, (char[]) value2);
            }
            if (value1 instanceof double[] && value2 instanceof double[]) {
                return ArrayUtil.equals((double[]) value1, (double[]) value2);
            }
            if (value1 instanceof float[] && value2 instanceof float[]) {
                return ArrayUtil.equals((float[]) value1, (float[]) value2);
            }
            if (value1 instanceof int[] && value2 instanceof int[]) {
                return ArrayUtil.equals((int[]) value1, (int[]) value2);
            }
            if (value1 instanceof long[] && value2 instanceof long[]) {
                return ArrayUtil.equals((long[]) value1, (long[]) value2);
            }
            if (value1 instanceof short[] && value2 instanceof short[]) {
                return ArrayUtil.equals((short[]) value1, (short[]) value2);
            }
        }

        return false;
    }

    public static int hashCode(Object value) {
        if (ObjectUtil.isAnyNull(value)) {
            return 0;
        }

        if (value.getClass().isArray()) {
            if (value instanceof Object[]) {
                return ArrayUtil.hashCode((Object[]) value);
            }
            if (value instanceof boolean[]) {
                return ArrayUtil.hashCode((boolean[]) value);
            }
            if (value instanceof byte[]) {
                return ArrayUtil.hashCode((byte[]) value);
            }
            if (value instanceof char[]) {
                return ArrayUtil.hashCode((char[]) value);
            }
            if (value instanceof double[]) {
                return ArrayUtil.hashCode((double[]) value);
            }
            if (value instanceof float[]) {
                return ArrayUtil.hashCode((float[]) value);
            }
            if (value instanceof int[]) {
                return ArrayUtil.hashCode((int[]) value);
            }
            if (value instanceof long[]) {
                return ArrayUtil.hashCode((long[]) value);
            }
            if (value instanceof short[]) {
                return ArrayUtil.hashCode((short[]) value);
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

    public static <T extends ISerializeCapable<?>> T readExternal(ObjectInput in) throws ClassNotFoundException,
            IOException {
        if (ObjectUtil.isAnyNull(in)) {
            throw new NullPointerException();
        }

        if (NumberUtil.readExternalBoolean(in)) {
            @SuppressWarnings("unchecked")
            T value = (T) in.readObject();
            return value;
        } else {
            return null;
        }
    }

    public static <T extends ISerializeCapable<?>> void writeExternal(ObjectOutput out, T value) throws IOException {
        if (ObjectUtil.isAnyNull(out)) {
            throw new NullPointerException();
        }

        if (value == null) {
            NumberUtil.writeExternalBoolean(out, false);
        } else {
            NumberUtil.writeExternalBoolean(out, true);
            out.writeObject(value);
        }
    }

    public static byte[] transferToByteArray(Object value) {
        if (ObjectUtil.isAnyNull(value)) {
            return null;
        }

        byte[] stream;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(value);
            objectOutputStream.flush();
            stream = byteArrayOutputStream.toByteArray();
            objectOutputStream.close();
            objectOutputStream.close();
        } catch (IOException ex) {
            throw new StatusUnexpectedException();
        }

        return stream;
    }

    @SuppressWarnings("unchecked")
    public static <T> T transferFromByteArray(byte[] stream) {
        if (ObjectUtil.isAnyNull(stream)) {
            return null;
        }

        Object object;

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(stream);
            ObjectInputStream outInputStream = new ObjectInputStream(byteArrayInputStream);
            object = outInputStream.readObject();
            outInputStream.close();
            byteArrayInputStream.close();
        } catch (IOException | ClassNotFoundException ex) {
            throw new StatusUnexpectedException();
        }

        if (ObjectUtil.isAnyNull(object)) {
            throw new StatusUnexpectedException();
        }

        try {
            return (T) object;
        } catch (ClassCastException e) {
            throw new StatusRelationshipErrorException();
        }
    }

    public static String transferToString(Object value) {
        try {
            return ObjectUtil.JSON_HELPER.writeValueAsString(value);
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
            return ObjectUtil.JSON_HELPER.readValue(value, clazz);
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

        JavaType type = ObjectUtil.JSON_HELPER.getTypeFactory().constructParametricType(List.class, clazz);

        try {
            return ObjectUtil.JSON_HELPER.readValue(value, type);
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

        JavaType type = ObjectUtil.JSON_HELPER.getTypeFactory().constructParametricType(Map.class, keyClass, valueClass);

        try {
            return ObjectUtil.JSON_HELPER.readValue(value, type);
        } catch (Exception ignored) {
            return defaultProvider.acquire();
        }
    }
}