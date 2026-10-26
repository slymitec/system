package indi.sly.subsystem.periphery.proxies.instances.security;

import indi.sly.subsystem.periphery.proxies.instances.IProxy;

import java.util.Set;
import java.util.UUID;

public interface IAccountSessionsProxy extends IProxy {
    Set<UUID> listSessions();

    void addSession(UUID sessionId);

    void deleteSession(UUID sessionId);
}
