package indi.sly.system.kernel.processes.instances.values;

public interface SignalType {
    long TYPE_UNKNOWN = 0L;
    long TYPE_CORE = 1L;
    long TYPE_FILES = 2L;
    long TYPE_OBJECTS = 3L;
    long TYPE_MEMORY = 4L;
    long TYPE_PROCESS = 5L;
    long TYPE_SECURITY = 6L;
    long TYPE_APPLICATION = -1L;

    long ACTION_UNKNOWN = 0L;
    long ACTION_ACCESS = 1L << 32;
    long ACTION_CREATE = 1L << 33;
    long ACTION_DELETE = 1L << 34;
    long ACTION_MODIFY = 1L << 35;
    long ACTION_BLEND = (Integer.MAX_VALUE * 2L + 1L) << 32;

    long RESULT_UNKNOWN = 0L;
    long RESULT_CANCEL = 1L;
    long RESULT_EXCEPTION = 2L;
    long RESULT_SUCCESS = 3L;
}
