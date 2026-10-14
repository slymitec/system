package indi.sly.system.common.supports;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class StringUtil {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final String EMPTY = "";

    public static boolean isNameIllegal(@Nullable CharSequence value) {
        if (ValueUtil.isAnyNullOrEmpty(value)) {
            return true;
        }
        if (value.length() > 256 || StringUtils.containsAny(value, '/', '\\', ':', '*', '?', '\"', '<', '>', '|', '$') || Strings.CI.equals(value, ".") || Strings.CI.equals(value, "..") || (value.length() > 1 && value.charAt(value.length() - 1) == '.')) {
            return true;
        }

        return false;
    }

    public static boolean isNameIllegalButWildcard(@Nullable CharSequence value) {
        if (ValueUtil.isAnyNullOrEmpty(value)) {
            return true;
        }
        if (value.length() > 256 || StringUtils.containsAny(value, '/', '\\', ':', '\"', '<', '>', '|', '$') || Strings.CI.equals(value, ".") || Strings.CI.equals(value, "..") || (value.length() > 1 && value.charAt(value.length() - 1) == '.')) {
            return true;
        }

        return false;
    }

    public static boolean isNameIllegal(@Nullable CharSequence... values) {
        for (CharSequence value : values) {
            if (StringUtil.isNameIllegal(value)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isNameIllegalButWildcard(@Nullable CharSequence... values) {
        for (CharSequence value : values) {
            if (StringUtil.isNameIllegalButWildcard(value)) {
                return true;
            }
        }

        return false;
    }

    public static String readFormBytes(byte @Nullable [] value) {
        if (ObjectUtil.isNull(value)) {
            throw new NullPointerException();
        }

        return new String(value, StringUtil.DEFAULT_CHARSET);
    }

    public static byte[] writeToBytes(@Nullable String value) {
        if (ObjectUtil.isNull(value)) {
            throw new NullPointerException();
        }

        return value.getBytes(StringUtil.DEFAULT_CHARSET);
    }
}