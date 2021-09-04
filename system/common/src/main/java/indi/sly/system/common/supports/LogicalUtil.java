package indi.sly.system.common.supports;

public abstract class LogicalUtil {
    public static long and(long... values) {
        if (ObjectUtil.isAnyNull(values)) {
            throw new NullPointerException();
        }

        long result = -1;

        for (long value : values) {
            result = result & value;
        }

        return result;
    }

    public static long or(long... values) {
        if (ObjectUtil.isAnyNull(values)) {
            throw new NullPointerException();
        }

        long result = 0;

        for (long value : values) {
            result = result | value;
        }

        return result;
    }

    public static boolean isAllExist(long source, long values) {
        if (values == 0) {
            return source == values;
        } else if ((source & values) == values) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isAnyExist(long source, long values) {
        if (values == 0) {
            return source == values;
        } else if ((source & values) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isAnyEqual(long source, long... values) {
        if (ObjectUtil.isAnyNull(values) || values.length == 0) {
            return false;
        }

        for (long value : values) {
            if (source == value) {
                return true;
            }
        }

        return false;
    }

    public static boolean allNotEqual(long source, long... values) {
        if (ObjectUtil.isAnyNull(values) || values.length == 0) {
            return true;
        }

        for (long value : values) {
            if (source == value) {
                return false;
            }
        }

        return true;
    }
}
