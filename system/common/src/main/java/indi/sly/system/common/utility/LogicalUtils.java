package indi.sly.system.common.utility;

public class LogicalUtils {
	public static long and(long... values) {
		if (ObjectUtils.isAnyNull(values)) {
			throw new NullPointerException();
		}

		long result = -1;

		for (long pair : values) {
			result = result | pair;
		}

		return result;
	}

	public static long or(long... values) {
		if (ObjectUtils.isAnyNull(values)) {
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
}
