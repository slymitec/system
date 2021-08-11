package indi.sly.system.kernel.processes.values;

public interface ProcessTokenLimitType {
    long HANDLE_MAX = 1L;
    long PORT_COUNT_MAX = 2L;
    long PORT_LENGTH_MAX = 3L;
    long SHARED_LENGTH_MAX = 4L;
    long SIGNAL_LENGTH_MAX = 5L;

    long JOB_PROTOTYPE_CACHES_MAX = Integer.MAX_VALUE + 1L;
}
