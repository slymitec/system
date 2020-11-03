package indi.sly.system.common.utility;

public class ArrayUtils {
    public static byte[] copyBytes(byte[] original) {
        if (ObjectUtils.isAnyNull(original)) {
            return null;
        }

        byte[] result = new byte[original.length];
        System.arraycopy(original, 0, result, 0, original.length);

        return result;
    }
}
