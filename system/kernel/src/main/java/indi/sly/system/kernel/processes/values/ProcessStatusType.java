package indi.sly.system.kernel.processes.values;

public interface ProcessStatusType {
    long NULL = 0L;
    long INITIALIZATION = 1l;
    long RUNNING = 2L;
    long INTERRUPTED = 3L;
    long DIED = 4L;
    long ZOMBIE = 5L;

}
