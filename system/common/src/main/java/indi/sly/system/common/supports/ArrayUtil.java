package indi.sly.system.common.supports;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.StringJoiner;

public abstract class ArrayUtil {
    public final static boolean[] EMPTY_BOOLEAN = new boolean[0];
    public final static byte[] EMPTY_BYTES = new byte[0];
    public final static char[] EMPTY_CHAR = new char[0];
    public final static double[] EMPTY_DOUBLE = new double[0];
    public final static float[] EMPTY_FLOAT = new float[0];
    public final static int[] EMPTY_INT = new int[0];
    public final static long[] EMPTY_LONG = new long[0];
    public final static short[] EMPTY_SHORT = new short[0];

    private static final int HASHCODE_INITIAL_VALUE = 7;
    private static final int HASHCODE_MULTIPLIER_VALUE = 31;

    private static final String TO_STRING_NULL_OBJECT = "null";
    private static final String TO_STRING_ARRAY_START = "{";
    private static final String TO_STRING_ARRAY_END = "}";
    private static final String TO_STRING_EMPTY_ARRAY =
            ArrayUtil.TO_STRING_ARRAY_START + ArrayUtil.TO_STRING_ARRAY_END;
    private static final String TO_STRING_ARRAY_ELEMENT_SEPARATOR = ", ";

    public static boolean isArray(Object object) {
        return (ObjectUtil.allNotNull(object) && object.getClass().isArray());
    }

    public static boolean isNullOrEmpty(final Object[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return true;
        } else {
            return Array.getLength(array) == 0;
        }
    }

    public static boolean isNullOrEmpty(final boolean[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return true;
        } else {
            return Array.getLength(array) == 0;
        }
    }

    public static boolean isNullOrEmpty(final byte[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return true;
        } else {
            return Array.getLength(array) == 0;
        }
    }

    public static boolean isNullOrEmpty(final char[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return true;
        } else {
            return Array.getLength(array) == 0;
        }
    }

    public static boolean isNullOrEmpty(final double[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return true;
        } else {
            return Array.getLength(array) == 0;
        }
    }

    public static boolean isNullOrEmpty(final float[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return true;
        } else {
            return Array.getLength(array) == 0;
        }
    }

    public static boolean isNullOrEmpty(final int[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return true;
        } else {
            return Array.getLength(array) == 0;
        }
    }

    public static boolean isNullOrEmpty(final long[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return true;
        } else {
            return Array.getLength(array) == 0;
        }
    }

    public static boolean isNullOrEmpty(final short[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return true;
        } else {
            return Array.getLength(array) == 0;
        }
    }

    public static boolean equals(Object[] value1, Object[] value2) {
        if (value1 == value2) {
            return true;
        } else if (value1 == null || value2 == null) {
            return false;
        } else {
            return Arrays.equals(value1, value2);
        }
    }

    public static boolean equals(boolean[] value1, boolean[] value2) {
        if (value1 == value2) {
            return true;
        } else if (value1 == null || value2 == null) {
            return false;
        } else {
            return Arrays.equals(value1, value2);
        }
    }

    public static boolean equals(byte[] value1, byte[] value2) {
        if (value1 == value2) {
            return true;
        } else if (value1 == null || value2 == null) {
            return false;
        } else {
            return Arrays.equals(value1, value2);
        }
    }

    public static boolean equals(char[] value1, char[] value2) {
        if (value1 == value2) {
            return true;
        } else if (value1 == null || value2 == null) {
            return false;
        } else {
            return Arrays.equals(value1, value2);
        }
    }

    public static boolean equals(double[] value1, double[] value2) {
        if (value1 == value2) {
            return true;
        } else if (value1 == null || value2 == null) {
            return false;
        } else {
            return Arrays.equals(value1, value2);
        }
    }

    public static boolean equals(float[] value1, float[] value2) {
        if (value1 == value2) {
            return true;
        } else if (value1 == null || value2 == null) {
            return false;
        } else {
            return Arrays.equals(value1, value2);
        }
    }

    public static boolean equals(int[] value1, int[] value2) {
        if (value1 == value2) {
            return true;
        } else if (value1 == null || value2 == null) {
            return false;
        } else {
            return Arrays.equals(value1, value2);
        }
    }

    public static boolean equals(long[] value1, long[] value2) {
        if (value1 == value2) {
            return true;
        } else if (value1 == null || value2 == null) {
            return false;
        } else {
            return Arrays.equals(value1, value2);
        }
    }

    public static boolean equals(short[] value1, short[] value2) {
        if (value1 == value2) {
            return true;
        } else if (value1 == null || value2 == null) {
            return false;
        } else {
            return Arrays.equals(value1, value2);
        }
    }

    public static int hashCode(Object[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return 0;
        }
        int hash = ArrayUtil.HASHCODE_INITIAL_VALUE;
        for (Object element : array) {
            hash = ArrayUtil.HASHCODE_MULTIPLIER_VALUE * hash + ObjectUtil.hashCode(element);
        }

        return hash;
    }

    public static int hashCode(boolean[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return 0;
        }
        int hash = ArrayUtil.HASHCODE_INITIAL_VALUE;
        for (boolean element : array) {
            hash = ArrayUtil.HASHCODE_MULTIPLIER_VALUE * hash + Boolean.hashCode(element);
        }

        return hash;
    }

    public static int hashCode(byte[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return 0;
        }
        int hash = ArrayUtil.HASHCODE_INITIAL_VALUE;
        for (byte element : array) {
            hash = ArrayUtil.HASHCODE_MULTIPLIER_VALUE * hash + element;
        }

        return hash;
    }

    public static int hashCode(char[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return 0;
        }
        int hash = ArrayUtil.HASHCODE_INITIAL_VALUE;
        for (char element : array) {
            hash = ArrayUtil.HASHCODE_MULTIPLIER_VALUE * hash + element;
        }

        return hash;
    }

    public static int hashCode(double[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return 0;
        }
        int hash = ArrayUtil.HASHCODE_INITIAL_VALUE;
        for (double element : array) {
            hash = ArrayUtil.HASHCODE_MULTIPLIER_VALUE * hash + Double.hashCode(element);
        }

        return hash;
    }

    public static int hashCode(float[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return 0;
        }
        int hash = ArrayUtil.HASHCODE_INITIAL_VALUE;
        for (float element : array) {
            hash = ArrayUtil.HASHCODE_MULTIPLIER_VALUE * hash + Float.hashCode(element);
        }

        return hash;
    }

    public static int hashCode(int[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return 0;
        }
        int hash = ArrayUtil.HASHCODE_INITIAL_VALUE;
        for (int element : array) {
            hash = ArrayUtil.HASHCODE_MULTIPLIER_VALUE * hash + element;
        }

        return hash;
    }

    public static int hashCode(long[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return 0;
        }
        int hash = ArrayUtil.HASHCODE_INITIAL_VALUE;
        for (long element : array) {
            hash = ArrayUtil.HASHCODE_MULTIPLIER_VALUE * hash + Long.hashCode(element);
        }

        return hash;
    }

    public static int hashCode(short[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return 0;
        }
        int hash = ArrayUtil.HASHCODE_INITIAL_VALUE;
        for (short element : array) {
            hash = ArrayUtil.HASHCODE_MULTIPLIER_VALUE * hash + element;
        }

        return hash;
    }

    public static String toString(Object[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return ArrayUtil.TO_STRING_NULL_OBJECT;
        }

        if (ArrayUtil.isNullOrEmpty(array)) {
            return ArrayUtil.TO_STRING_EMPTY_ARRAY;
        }

        StringJoiner stringJoiner = new StringJoiner(ArrayUtil.TO_STRING_ARRAY_ELEMENT_SEPARATOR,
                ArrayUtil.TO_STRING_ARRAY_START, ArrayUtil.TO_STRING_ARRAY_END);
        for (Object element : array) {
            stringJoiner.add(ObjectUtil.toString(element));
        }
        return stringJoiner.toString();
    }

    public static String toString(boolean[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return ArrayUtil.TO_STRING_NULL_OBJECT;
        }

        if (ArrayUtil.isNullOrEmpty(array)) {
            return ArrayUtil.TO_STRING_EMPTY_ARRAY;
        }

        StringJoiner stringJoiner = new StringJoiner(ArrayUtil.TO_STRING_ARRAY_ELEMENT_SEPARATOR,
                ArrayUtil.TO_STRING_ARRAY_START, ArrayUtil.TO_STRING_ARRAY_END);
        for (Object element : array) {
            stringJoiner.add(String.valueOf(element));
        }
        return stringJoiner.toString();
    }

    public static String toString(byte[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return ArrayUtil.TO_STRING_NULL_OBJECT;
        }

        if (ArrayUtil.isNullOrEmpty(array)) {
            return ArrayUtil.TO_STRING_EMPTY_ARRAY;
        }

        StringJoiner stringJoiner = new StringJoiner(ArrayUtil.TO_STRING_ARRAY_ELEMENT_SEPARATOR,
                ArrayUtil.TO_STRING_ARRAY_START, ArrayUtil.TO_STRING_ARRAY_END);
        for (Object element : array) {
            stringJoiner.add(String.valueOf(element));
        }
        return stringJoiner.toString();
    }

    public static String toString(char[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return ArrayUtil.TO_STRING_NULL_OBJECT;
        }

        if (ArrayUtil.isNullOrEmpty(array)) {
            return ArrayUtil.TO_STRING_EMPTY_ARRAY;
        }

        StringJoiner stringJoiner = new StringJoiner(ArrayUtil.TO_STRING_ARRAY_ELEMENT_SEPARATOR,
                ArrayUtil.TO_STRING_ARRAY_START, ArrayUtil.TO_STRING_ARRAY_END);
        for (Object element : array) {
            stringJoiner.add(String.valueOf(element));
        }
        return stringJoiner.toString();
    }

    public static String toString(double[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return ArrayUtil.TO_STRING_NULL_OBJECT;
        }

        if (ArrayUtil.isNullOrEmpty(array)) {
            return ArrayUtil.TO_STRING_EMPTY_ARRAY;
        }

        StringJoiner stringJoiner = new StringJoiner(ArrayUtil.TO_STRING_ARRAY_ELEMENT_SEPARATOR,
                ArrayUtil.TO_STRING_ARRAY_START, ArrayUtil.TO_STRING_ARRAY_END);
        for (Object element : array) {
            stringJoiner.add(String.valueOf(element));
        }
        return stringJoiner.toString();
    }

    public static String toString(float[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return ArrayUtil.TO_STRING_NULL_OBJECT;
        }

        if (ArrayUtil.isNullOrEmpty(array)) {
            return ArrayUtil.TO_STRING_EMPTY_ARRAY;
        }

        StringJoiner stringJoiner = new StringJoiner(ArrayUtil.TO_STRING_ARRAY_ELEMENT_SEPARATOR,
                ArrayUtil.TO_STRING_ARRAY_START, ArrayUtil.TO_STRING_ARRAY_END);
        for (Object element : array) {
            stringJoiner.add(String.valueOf(element));
        }
        return stringJoiner.toString();
    }

    public static String toString(int[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return ArrayUtil.TO_STRING_NULL_OBJECT;
        }

        if (ArrayUtil.isNullOrEmpty(array)) {
            return ArrayUtil.TO_STRING_EMPTY_ARRAY;
        }

        StringJoiner stringJoiner = new StringJoiner(ArrayUtil.TO_STRING_ARRAY_ELEMENT_SEPARATOR,
                ArrayUtil.TO_STRING_ARRAY_START, ArrayUtil.TO_STRING_ARRAY_END);
        for (Object element : array) {
            stringJoiner.add(String.valueOf(element));
        }
        return stringJoiner.toString();
    }

    public static String toString(long[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return ArrayUtil.TO_STRING_NULL_OBJECT;
        }

        if (ArrayUtil.isNullOrEmpty(array)) {
            return ArrayUtil.TO_STRING_EMPTY_ARRAY;
        }

        StringJoiner stringJoiner = new StringJoiner(ArrayUtil.TO_STRING_ARRAY_ELEMENT_SEPARATOR,
                ArrayUtil.TO_STRING_ARRAY_START, ArrayUtil.TO_STRING_ARRAY_END);
        for (Object element : array) {
            stringJoiner.add(String.valueOf(element));
        }
        return stringJoiner.toString();
    }

    public static String toString(short[] array) {
        if (ObjectUtil.isAnyNull(array)) {
            return ArrayUtil.TO_STRING_NULL_OBJECT;
        }

        if (ArrayUtil.isNullOrEmpty(array)) {
            return ArrayUtil.TO_STRING_EMPTY_ARRAY;
        }

        StringJoiner stringJoiner = new StringJoiner(ArrayUtil.TO_STRING_ARRAY_ELEMENT_SEPARATOR,
                ArrayUtil.TO_STRING_ARRAY_START, ArrayUtil.TO_STRING_ARRAY_END);
        for (Object element : array) {
            stringJoiner.add(String.valueOf(element));
        }
        return stringJoiner.toString();
    }

    public static byte[] acquireBytes(byte[] original, int offset, int length) {
        if (original == null) {
            throw new NullPointerException();
        }
        if (offset < 0 || offset + length >= original.length) {
            throw new ArrayIndexOutOfBoundsException();
        }

        byte[] result = new byte[length];
        System.arraycopy(original, offset, result, 0, length);

        return result;
    }

    public static byte[] copyBytes(byte[] original) {
        if (ObjectUtil.isAnyNull(original)) {
            return null;
        }

        byte[] result = new byte[original.length];
        System.arraycopy(original, 0, result, 0, original.length);

        return result;
    }

    public static byte[] insertBytes(byte[] original, byte[] target, int targetOffset) {
        if (ObjectUtil.isAnyNull(target) || targetOffset < 0 || targetOffset >= target.length
                || (target.length - targetOffset) < (ObjectUtil.isAnyNull(original) ? 0 : original.length)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ObjectUtil.isAnyNull(original)) {
            return target;
        }

        System.arraycopy(original, 0, target, targetOffset, original.length);

        return target;
    }

    public static byte[] combineBytes(byte[]... values) {
        if (values == null) {
            throw new NullPointerException();
        }
        for (byte[] value : values) {
            if (ObjectUtil.isAnyNull(value)) {
                throw new NullPointerException();
            }
        }

        int offset = 0;
        for (byte[] value : values) {
            offset = offset + value.length;
        }

        byte[] result = new byte[offset];

        offset = 0;
        for (byte[] value : values) {
            System.arraycopy(value, 0, result, offset, value.length);
            offset = offset + value.length;
        }

        return result;
    }
}