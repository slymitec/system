package indi.sly.system.kernel.core.boot.types;

public interface StartupTypes {
    long STEP_INIT = 1L;
    long STEP_KERNEL = 1L << 1;
    long STEP_DRIVER = 1L << 2;
    long STEP_SERVICE = 1L << 3;
}
