package indi.sly.subsystem.periphery.proxies.instances.processes;

import indi.sly.subsystem.periphery.proxies.instances.IProxy;

public interface IProcessStatusProxy extends IProxy {
    long get();

    void run();

    void interrupt();
}