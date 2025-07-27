package indi.sly.clisubsystem.periphery.core.boot.prototypes;

public interface IStartupCapable {
    void startup(long startup);

    void shutdown();
}
