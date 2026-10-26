package indi.sly.subsystem.periphery.proxies.instances.processes;

import indi.sly.subsystem.periphery.proxies.instances.IProxy;

import java.util.UUID;

public interface IProcessSessionProxy extends IProxy {
    UUID getId();

    void setId(UUID id);

    long getType();

    void setType(long type);
}