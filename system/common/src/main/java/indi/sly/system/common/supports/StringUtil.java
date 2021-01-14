package indi.sly.system.common.supports;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class StringUtil extends org.apache.commons.lang3.StringUtils {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static boolean isNameIllegal(CharSequence value) {
        if (ValueUtil.isAnyNullOrEmpty(value)) {
            return true;
        }
        if (value.length() > 256 || StringUtil.containsAny(value, '/', '\\', ':', '*', '?', '\"', '<', '>', '|') || StringUtil.equalsIgnoreCase(value, ".")
                || StringUtil.equalsIgnoreCase(value, "..") || (value.length() > 1 && value.charAt(value.length() - 1) == '.')) {
            return true;
        }

        return false;
    }

    public static boolean isNameIllegal(CharSequence... values) {
        for (CharSequence value : values) {
            if (StringUtil.isNameIllegal(values)) {
                return true;
            }
        }

        return false;
    }

    public static String readFormBytes(byte[] value) {
        if (ObjectUtil.isAnyNull(value)) {
            throw new NullPointerException();
        }

        return new String(value, DEFAULT_CHARSET);
    }

    public static byte[] writeToBytes(String value) {
        if (ObjectUtil.isAnyNull(value)) {
            throw new NullPointerException();
        }

        return value.getBytes(DEFAULT_CHARSET);
    }

    public static String readExternal(ObjectInput in) throws IOException {
        if (ObjectUtil.isAnyNull(in)) {
            throw new NullPointerException();
        }

        return NumberUtil.readExternalBoolean(in) ? in.readUTF() : null;
    }

    public static void writeExternal(ObjectOutput out, String value) throws IOException {
        if (ObjectUtil.isAnyNull(out)) {
            throw new NullPointerException();
        }

        if (value == null) {
            NumberUtil.writeExternalBoolean(out, false);
        } else {
            NumberUtil.writeExternalBoolean(out, true);
            out.writeUTF(value);
        }
    }
}