package indi.sly.system.common.supports;

public abstract class LogicalUtil {
    public static long and(long... values) {
        if (ObjectUtil.isAnyNull(values)) {
            throw new NullPointerException();
        }

        long result = -1L;

        for (long value : values) {
            result = result & value;
        }

        return result;
    }

    public static long or(long... values) {
        if (ObjectUtil.isAnyNull(values)) {
            throw new NullPointerException();
        }

        long result = 0L;

        for (long value : values) {
            result = result | value;
        }

        return result;
    }

    public static boolean isAllExist(long source, long values) {
        if (values == 0L) {
            return source == values;
        } else if ((source & values) == values) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isAnyExist(long source, long values) {
        if (values == 0L) {
            return source == values;
        } else if ((source & values) == 0L) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isAnyEqual(long source, long value) {
        return source == value;
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

    public static boolean allNotEqual(long source, long value) {
        return source != value;
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

    public static boolean isAllSingleValue(long value) {
        if (value == 0L || ((value) & (value - 1)) != 0L) {
            return false;
        }

        return true;
    }

    public static boolean isAllSingleValue(long... values) {
        if (ObjectUtil.isAnyNull(values) || values.length == 0) {
            return false;
        }

        for (long value : values) {
            if (value == 0L || ((value) & (value - 1)) != 0L) {
                return false;
            }
        }

        return true;
    }
}
