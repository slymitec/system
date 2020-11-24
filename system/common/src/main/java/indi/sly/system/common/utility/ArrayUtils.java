package indi.sly.system.common.utility;

public class ArrayUtils extends org.apache.commons.lang3.ArrayUtils {
    public final static byte[] EMPTY_BYTES = new byte[0];

    public static byte[] copyBytes(byte[] original) {
        if (ObjectUtils.isAnyNull(original)) {
            return null;
        }

        byte[] result = new byte[original.length];
        System.arraycopy(original, 0, result, 0, original.length);

        return result;
    }

    public static byte[] insertBytes(byte[] original, byte[] target, int targetOffset) {
        if (ObjectUtils.isAnyNull(target) || targetOffset < 0 || targetOffset >= target.length
                || (target.length - targetOffset) < (ObjectUtils.isAnyNull(original) ? 0 : original.length)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (ObjectUtils.isAnyNull(original)) {
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
            if (ObjectUtils.isAnyNull(value)) {
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
