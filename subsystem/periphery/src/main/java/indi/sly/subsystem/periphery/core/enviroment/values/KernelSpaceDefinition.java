package indi.sly.subsystem.periphery.core.enviroment.values;

import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Named
@Singleton
public class KernelSpaceDefinition extends ASpaceDefinition {
    public KernelSpaceDefinition() {
        this.configuration = new KernelConfigurationDefinition();
        this.connections = new ConcurrentHashMap<>();
        this.namedConnectionIds = new ConcurrentHashMap<>();
        this.userSpace = new UserSpaceDefinition();
    }

    private final KernelConfigurationDefinition configuration;
    private final Map<UUID, ConnectionDefinition> connections;
    private final Map<String, UUID> namedConnectionIds;
    private final UserSpaceDefinition userSpace;

    public Map<UUID, ConnectionDefinition> getConnections() {
        return this.connections;
    }

    public Map<String, UUID> getNamedConnectionIds() {
        return this.namedConnectionIds;
    }

    public KernelConfigurationDefinition getConfiguration() {
        return configuration;
    }

    public UserSpaceDefinition getUserSpace() {
        return this.userSpace;
    }
}
