package indi.sly.system.common.supports;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusDisabilityException;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

public abstract class UUIDUtil {
    public static final UUID EMPTY = new UUID(0, 0);

    public static UUID createRandom() {
        return UUID.randomUUID();
    }

    public static UUID getEmpty() {
        return UUIDUtil.EMPTY;
    }

    public static UUID getFromBytes(byte[] value) {
        return UUID.nameUUIDFromBytes(value);
    }

    public static UUID getFormLongs(long most, long least) {
        return new UUID(most, least);
    }

    public static String toString(UUID uuid) {
        if (ObjectUtil.isAnyNull(uuid)) {
            return null;
        } else {
            return uuid.toString().replace("-", StringUtil.EMPTY);
        }
    }

    public static UUID readFormBytes(byte[] value) throws StatusDisabilityException {
        if (ObjectUtil.isAnyNull(value) || value.length != 16) {
            throw new ConditionParametersException();
        }

        return new UUID(
                ((long) value[0] & 0xFF) << 56 | ((long) value[1] & 0xFF) << 48 | ((long) value[2] & 0xFF) << 40 | ((long) value[3] & 0xFF) << 32 | ((long) value[4] & 0xFF) << 24
                        | ((long) value[5] & 0xFF) << 16 | ((long) value[6] & 0xFF) << 8 | ((long) value[7] & 0xFF),
                ((long) value[8] & 0xFF) << 56 | ((long) value[9] & 0xFF) << 48 | ((long) value[10] & 0xFF) << 40 | ((long) value[11] & 0xFF) << 32 | ((long) value[12] & 0xFF) << 24
                        | ((long) value[13] & 0xFF) << 16 | ((long) value[14] & 0xFF) << 8 | ((long) value[15] & 0xFF));
    }

    public static byte[] writeToBytes(UUID value) {
        if (ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
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
        if (ObjectUtil.isAnyNull(in)) {
            throw new NullPointerException();
        }

        return NumberUtil.readExternalBoolean(in) ? UUIDUtil.getFormLongs(NumberUtil.readExternalLong(in),
                NumberUtil.readExternalLong(in)) : null;
    }

    public static void writeExternal(ObjectOutput out, UUID value) throws IOException {
        if (ObjectUtil.isAnyNull(out)) {
            throw new NullPointerException();
        }

        if (value == null) {
            NumberUtil.writeExternalBoolean(out, false);
        } else {
            NumberUtil.writeExternalBoolean(out, true);
            NumberUtil.writeExternalLong(out, value.getMostSignificantBits());
            NumberUtil.writeExternalLong(out, value.getLeastSignificantBits());
        }
    }

}