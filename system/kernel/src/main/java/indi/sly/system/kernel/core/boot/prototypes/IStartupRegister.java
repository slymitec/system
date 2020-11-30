package indi.sly.system.kernel.core.boot.prototypes;

public interface IStartupRegister {
    void startup(long startupTypes);

    void shutdown();
}
