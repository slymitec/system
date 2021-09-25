package indi.sly.system.services.job.values;

public interface JobAttributeType {
    long NULL = 0L;
    long HAS_PROCESS = 1L;
    long HAS_NOT_TRANSACTION = 1L << 1;
}
