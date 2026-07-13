package indi.sly.subsystem.periphery.core.environment.containers;

import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Named
@Singleton
public class KernelSpace extends ASystemSpace {
    public KernelSpace() {
        this.configuration = new PeripheryConfiguration();
        this.connections = new ConcurrentHashMap<>();
        this.namedConnectionIds = new ConcurrentHashMap<>();
        this.userSpace = new UserSpace();
    }

    private final PeripheryConfiguration configuration;
    private final Map<UUID, ConnectionDefinition> connections;
    private final Map<String, UUID> namedConnectionIds;
    private final UserSpace userSpace;

    public Map<UUID, ConnectionDefinition> getConnections() {
        return this.connections;
    }

    public Map<String, UUID> getNamedConnectionIds() {
        return this.namedConnectionIds;
    }

    public PeripheryConfiguration getConfiguration() {
        return configuration;
    }

    public UserSpace getUserSpace() {
        return this.userSpace;
    }
}
