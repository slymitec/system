package indi.sly.system.common.supports;

import indi.sly.system.common.lang.ConditionParametersException;

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

    public static UUID getFormLongs(long mostSigBits, long leastSigBits) {
        return new UUID(mostSigBits, leastSigBits);
    }

//    public static UUID getFromString(String value) {
//        return UUIDUtil.getFromStringOrDefaultProvider(value, () -> null);
//    }
//
//    public static UUID getParameterOrDefault(String value, UUID defaultValue) {
//        return UUIDUtil.getFromStringOrDefaultProvider(value, () -> defaultValue);
//    }
//
//    public static UUID getFromStringOrDefaultProvider(String value, Provider<UUID> defaultValue) {
//        if (ValueUtil.isAnyNullOrEmpty(value) || ObjectUtil.isAnyNull(defaultValue)) {
//            throw new ConditionParametersException();
//        }
//
//        if (value.length() == 36) {
//            value = value.replace("-", "");
//        }
//        if (value.length() != 32) {
//            return defaultValue.acquire();
//        }
//        for (int i = 0; i < 32; i++) {
//            char pair = value.charAt(i);
//            if (pair < '0' || (pair > '9' && pair < 'A') || (pair > 'F' && pair < 'a') || pair > 'f') {
//                return defaultValue.acquire();
//            }
//        }
//
//        long mostSigBits = Long.parseLong(value, 0, 8, 16) & 0xffffffffL;
//        mostSigBits <<= 32;
//        mostSigBits |= Long.parseLong(value, 8, 16, 16) & 0xffffffffL;
//
//        long leastSigBits = Long.parseLong(value, 16, 24, 16) & 0xffffffffL;
//        leastSigBits <<= 32;
//        leastSigBits |= Long.parseLong(value, 24, 32, 16) & 0xffffffffL;
//
//        return new UUID(mostSigBits, leastSigBits);
//    }

    public static UUID readFormBytes(byte[] value) {
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