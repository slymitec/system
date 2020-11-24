package indi.sly.system.common.utility;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static boolean isNameIllegal(CharSequence... values) {
        if (StringUtils.isAnyNullOrEmpty(values)) {
            return true;
        }

        for (CharSequence pair : values) {
            if (pair.length() > 256 || StringUtils.containsAny(pair, '/', '\\', ':', '*', '?', '\"', '<', '>', '|') || StringUtils.equalsIgnoreCase(pair, ".")
                    || StringUtils.equalsIgnoreCase(pair, "..") || (pair.length() > 1 && pair.charAt(pair.length() - 1) == '.')) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAnyNullOrEmpty(CharSequence... value) {
        if (value == null || value.length == 0) {
            return true;
        }
        for (CharSequence pair : value) {
            if (pair == null) {
                throw new NullPointerException();
            } else if (pair.length() == 0) {
                throw new NullPointerException();
            }
        }

        return false;
    }

    public static String readFormBytes(byte[] value) {
        if (ObjectUtils.isAnyNull(value)) {
            throw new NullPointerException();
        }

        return new String(value, DEFAULT_CHARSET);
    }

    public static byte[] writeToBytes(String value) {
        if (ObjectUtils.isAnyNull(value)) {
            throw new NullPointerException();
        }

        return value.getBytes(DEFAULT_CHARSET);
    }

    public static String readExternal(ObjectInput in) throws IOException {
        if (ObjectUtils.isAnyNull(in)) {
            throw new NullPointerException();
        }

        return NumberUtils.readExternalBoolean(in) ? in.readUTF() : null;
    }

    public static void writeExternal(ObjectOutput out, String value) throws IOException {
        if (ObjectUtils.isAnyNull(out)) {
            throw new NullPointerException();
        }

        if (value == null) {
            NumberUtils.writeExternalBoolean(out, false);
        } else {
            NumberUtils.writeExternalBoolean(out, true);
            out.writeUTF(value);
        }
    }
}