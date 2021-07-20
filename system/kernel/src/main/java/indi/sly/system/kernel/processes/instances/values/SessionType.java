package indi.sly.system.kernel.processes.instances.values;

public interface SessionType {
    long NULL = 0L;
    long API = 1L;
    long CLI = 1L << 1 | SessionType.API;
    long GUI = 1L << 2 | SessionType.API;
}
