package indi.sly.subsystem.periphery.proxies.instances.processes;

import indi.sly.subsystem.periphery.proxies.instances.IProxy;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface IProcessTokenProxy extends IProxy {
    UUID getAccountId();

    long getPrivileges();

    void setPrivileges(long privileges);

    Map<Long, Integer> getLimits();

    void setLimits(Map<Long, Integer> limits);

    Set<UUID> getRoles();

    void initDefaultRoles();

    void addRoles(Set<UUID> roles);
}