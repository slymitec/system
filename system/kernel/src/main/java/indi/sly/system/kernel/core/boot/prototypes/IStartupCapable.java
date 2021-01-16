package indi.sly.system.kernel.core.boot.prototypes;

public interface IStartupCapable {
    void startup(long startupTypes);

    void shutdown();
}
