package indi.sly.system.common.supports;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class StringUtil {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final String EMPTY = "";

    public static boolean isNameIllegal(CharSequence value) {
        if (ValueUtil.isAnyNullOrEmpty(value)) {
            return true;
        }
        if (value.length() > 256 || StringUtils.containsAny(value, '/', '\\', ':', '*', '?', '\"', '<', '>', '|') || StringUtils.equalsIgnoreCase(value, ".")
                || StringUtils.equalsIgnoreCase(value, "..") || (value.length() > 1 && value.charAt(value.length() - 1) == '.')) {
            return true;
        }

        return false;
    }

    public static boolean isNameIllegal(CharSequence... values) {
        for (CharSequence value : values) {
            if (StringUtil.isNameIllegal(value)) {
                return true;
            }
        }

        return false;
    }

    public static String readFormBytes(byte[] value) {
        if (ObjectUtil.isAnyNull(value)) {
            throw new NullPointerException();
        }

        return new String(value, StringUtil.DEFAULT_CHARSET);
    }

    public static byte[] writeToBytes(String value) {
        if (ObjectUtil.isAnyNull(value)) {
            throw new NullPointerException();
        }

        return value.getBytes(StringUtil.DEFAULT_CHARSET);
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