package indi.sly.system.kernel.processes.values;

public interface ThreadStatusType {
    long NULL = 0L;
    long INITIALIZATION = 1L;
    long RUNNING = 2L;
    long DIED = 4L;
}
