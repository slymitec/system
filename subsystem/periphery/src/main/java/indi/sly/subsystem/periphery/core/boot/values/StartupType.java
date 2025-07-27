package indi.sly.subsystem.periphery.core.boot.values;

public interface StartupType {
    long SHUTDOWN = 0L;
    long STEP_INIT_SELF = 1L;
    long STEP_AFTER_SELF = 2L;
    long STEP_INIT_PERIPHERY = Integer.MAX_VALUE + 1L;
    long STEP_AFTER_PERIPHERY = Integer.MAX_VALUE + 2L;
}
