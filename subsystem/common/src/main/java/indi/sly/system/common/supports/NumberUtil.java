package indi.sly.system.common.supports;

import indi.sly.system.common.lang.StatusDisabilityException;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class NumberUtil {
    public static boolean readExternalBoolean(ObjectInput in) throws IOException {
        if (ObjectUtil.isAnyNull(in)) {
            throw new NullPointerException();
        }

        return in.readByte() > 0;
    }

    public static byte readExternalByte(ObjectInput in) throws IOException {
        if (ObjectUtil.isAnyNull(in)) {
            throw new NullPointerException();
        }

        return in.readByte();
    }

    public static byte[] readExternalBytes(ObjectInput in) throws IOException {
        if (ObjectUtil.isAnyNull(in)) {
            throw new NullPointerException();
        }

        int length = NumberUtil.readExternalInteger(in);
        if (length < 0) {
            if (length == -1) {
                return null;
            } else {
                throw new StatusDisabilityException();
            }
        } else {
            byte[] value = new byte[length];
            in.read(value, 0, length);
            return value;
        }
    }

    public static short readExternalShort(ObjectInput in) throws IOException {
        if (ObjectUtil.isAnyNull(in)) {
            throw new NullPointerException();
        }

        return in.readShort();
    }

    public static int readExternalInteger(ObjectInput in) throws IOException {
        if (ObjectUtil.isAnyNull(in)) {
            throw new NullPointerException();
        }

        return in.readInt();
    }

    public static long readExternalLong(ObjectInput in) throws IOException {
        if (ObjectUtil.isAnyNull(in)) {
            throw new NullPointerException();
        }

        return in.readLong();
    }

    public static void writeExternalBoolean(ObjectOutput out, Boolean value) throws IOException {
        if (ObjectUtil.isAnyNull(out)) {
            throw new NullPointerException();
        }

        out.writeByte(value ? 1 : 0);
    }

    public static void writeExternalByte(ObjectOutput out, byte value) throws IOException {
        if (ObjectUtil.isAnyNull(out)) {
            throw new NullPointerException();
        }

        out.writeByte(value);
    }

    public static void writeExternalBytes(ObjectOutput out, byte[] value) throws IOException {
        if (ObjectUtil.isAnyNull(out)) {
            throw new NullPointerException();
        }

        if (value == null) {
            NumberUtil.writeExternalInteger(out, -1);
        } else {
            NumberUtil.writeExternalInteger(out, value.length);
            out.write(value);
        }
    }

    public static void writeExternalShort(ObjectOutput out, short value) throws IOException {
        if (ObjectUtil.isAnyNull(out)) {
            throw new NullPointerException();
        }

        out.writeShort(value);
    }

    public static void writeExternalInteger(ObjectOutput out, int value) throws IOException {
        if (ObjectUtil.isAnyNull(out)) {
            throw new NullPointerException();
        }

        out.writeInt(value);
    }

    public static void writeExternalLong(ObjectOutput out, long value) throws IOException {
        if (ObjectUtil.isAnyNull(out)) {
            throw new NullPointerException();
        }

        out.writeLong(value);
    }
}