package indi.sly.system.common.utility;

import indi.sly.system.common.exceptions.StatusDisabilityException;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

public class UUIDUtils {
    private static final UUID EMPTY = new UUID(0, 0);

    public static UUID getFromBytes(byte[] value) {
        return UUID.nameUUIDFromBytes(value);
    }

    public static UUID getFormLongs(long most, long least) {
        return new UUID(most, least);
    }

    public static UUID getEmpty() {
        return UUIDUtils.EMPTY;
    }

    public static UUID createRandom() {
        return UUID.randomUUID();
    }

    public static boolean isAnyNullOrEmpty(UUID... values) {
        if (values == null || values.length == 0) {
            return true;
        }
        for (UUID pair : values) {
            if (pair == null || pair.equals(UUIDUtils.EMPTY)) {
                return true;
            }
        }

        return false;
    }

    public static String transString(UUID value) {
        return StringUtils.replace(value.toString(), "-", "");
    }

    public static UUID readFormBytes(byte[] value) throws StatusDisabilityException {
        if (ObjectUtils.isAnyNull(value)) {
            throw new NullPointerException();
        }

        if (value.length != 16) {
            throw new StatusDisabilityException();
        }

        return new UUID(
                ((long) value[0] & 0xFF) << 56 | ((long) value[1] & 0xFF) << 48 | ((long) value[2] & 0xFF) << 40 | ((long) value[3] & 0xFF) << 32 | ((long) value[4] & 0xFF) << 24
                        | ((long) value[5] & 0xFF) << 16 | ((long) value[6] & 0xFF) << 8 | ((long) value[7] & 0xFF),
                ((long) value[8] & 0xFF) << 56 | ((long) value[9] & 0xFF) << 48 | ((long) value[10] & 0xFF) << 40 | ((long) value[11] & 0xFF) << 32 | ((long) value[12] & 0xFF) << 24
                        | ((long) value[13] & 0xFF) << 16 | ((long) value[14] & 0xFF) << 8 | ((long) value[15] & 0xFF));
    }

    public static byte[] writeToBytes(UUID value) {
        if (ObjectUtils.isAnyNull(value)) {
            throw new NullPointerException();
        }

        byte[] result = new byte[16];

        long number = value.getMostSignificantBits();
        result[0] = (byte) (number >>> 56);
        result[1] = (byte) (number >>> 48);
        result[2] = (byte) (number >>> 40);
        result[3] = (byte) (number >>> 32);
        result[4] = (byte) (number >>> 24);
        result[5] = (byte) (number >>> 16);
        result[6] = (byte) (number >>> 8);
        result[7] = (byte) (number);

        number = value.getLeastSignificantBits();
        result[8] = (byte) (number >>> 56);
        result[9] = (byte) (number >>> 48);
        result[10] = (byte) (number >>> 40);
        result[11] = (byte) (number >>> 32);
        result[12] = (byte) (number >>> 24);
        result[13] = (byte) (number >>> 16);
        result[14] = (byte) (number >>> 8);
        result[15] = (byte) (number);

        return result;
    }

    public static UUID readExternal(ObjectInput in) throws IOException {
        if (ObjectUtils.isAnyNull(in)) {
            throw new NullPointerException();
        }

        return NumberUtils.readExternalBoolean(in) ? UUIDUtils.getFormLongs(NumberUtils.readExternalLong(in), NumberUtils.readExternalLong(in)) : null;
    }

    public static void writeExternal(ObjectOutput out, UUID value) throws IOException {
        if (ObjectUtils.isAnyNull(out)) {
            throw new NullPointerException();
        }

        if (value == null) {
            NumberUtils.writeExternalBoolean(out, false);
        } else {
            NumberUtils.writeExternalBoolean(out, true);
            NumberUtils.writeExternalLong(out, value.getMostSignificantBits());
            NumberUtils.writeExternalLong(out, value.getLeastSignificantBits());
        }
    }
}