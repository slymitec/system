package indi.sly.subsystem.periphery.proxies.instances.processes;

import indi.sly.subsystem.periphery.proxies.instances.IProxy;

import java.util.Set;
import java.util.UUID;

public interface IProcessInfoTableProxy extends IProxy {
    Set<UUID> list();

    boolean containByIndex(UUID index);

    boolean containById(UUID id);

    IProcessInfoEntryProxy getByIndex(UUID index);

    IProcessInfoEntryProxy getById(UUID id);
}