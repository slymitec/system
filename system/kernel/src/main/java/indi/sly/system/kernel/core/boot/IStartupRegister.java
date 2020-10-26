package indi.sly.system.kernel.core.boot;

public interface IStartupRegister {
    void startup(long startupTypes);

    void shutdown();
}
