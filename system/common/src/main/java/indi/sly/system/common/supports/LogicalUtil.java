package indi.sly.system.common.supports;

public abstract class LogicalUtil {
    public static long and(long... values) {
        if (ObjectUtil.isAnyNull(values)) {
            throw new NullPointerException();
        }

        long result = -1;

        for (long pair : values) {
            result = result & pair;
        }

        return result;
    }

    public static long or(long... values) {
        if (ObjectUtil.isAnyNull(values)) {
            throw new NullPointerException();
        }

        long result = 0;

        for (long pair : values) {
            result = result | pair;
        }

        return result;
    }

    public static boolean isAllExist(long value, long values) {
        if ((value & values) == values) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isAnyExist(long value, long values) {
        if ((value & values) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isNotAllExist(long value, long values) {
        if ((value & values) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNotAnyExist(long value, long values) {
        if ((value & values) == values) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isAnyEqual(long value, long... values) {
        if (ObjectUtil.isAnyNull(values) || values.length == 0) {
            return false;
        }

        for (long pair : values) {
            if (value == pair) {
                return true;
            }
        }

        return false;
    }

    public static boolean allNotEqual(long value, long... values) {
        if (ObjectUtil.isAnyNull(values) || values.length == 0) {
            return true;
        }

        for (long pair : values) {
            if (value == pair) {
                return false;
            }
        }

        return true;
    }
}
