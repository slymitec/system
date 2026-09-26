package indi.sly.system.kernel.core.environment.values;

public interface CacheDurationType {
    long PREPARE = 0L;
    long RUNNING = 1L;
    long PERMANENT = -1L;
}