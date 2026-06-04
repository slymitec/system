package indi.sly.system.services.jobs.values;

public interface TaskAttributeType {
    long NULL = 0L;
    long HAS_PROCESS = 1L;
    long HAS_NOT_TRANSACTION = 1L << 1;
    long OBJECT_IS_NOT_CACHEABLE = 1L << 2;
}
