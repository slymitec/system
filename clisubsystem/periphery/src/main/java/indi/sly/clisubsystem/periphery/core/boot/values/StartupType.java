package indi.sly.clisubsystem.periphery.core.boot.values;

public interface StartupType {
    long SHUTDOWN = 0L;
    long STEP_INIT_SELF = 1L;
    long STEP_AFTER_SELF = 2L;
    long STEP_INIT_KERNEL = 3L;
    long STEP_AFTER_KERNEL = 4L;
    long STEP_INIT_SERVICE = 5L;
    long STEP_AFTER_SERVICE = 6L;
}
