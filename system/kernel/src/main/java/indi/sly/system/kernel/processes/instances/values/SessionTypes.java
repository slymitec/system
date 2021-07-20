package indi.sly.system.kernel.processes.instances.values;

public interface SessionTypes {
    long NULL = 0L;
    long API = 1L;
    long CLI = 1L << 1 | SessionTypes.API;
    long GUI = 1L << 2 | SessionTypes.API;
}
