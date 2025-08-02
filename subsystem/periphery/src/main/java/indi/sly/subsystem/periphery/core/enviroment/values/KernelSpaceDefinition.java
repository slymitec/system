package indi.sly.subsystem.periphery.core.enviroment.values;

import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Named
@Singleton
public class KernelSpaceDefinition extends ASpaceDefinition<KernelSpaceDefinition> {
    public KernelSpaceDefinition() {
        this.configuration = new KernelConfigurationDefinition();
        this.connections = new ConcurrentHashMap<>();
        this.namedConnectionIDs = new ConcurrentHashMap<>();
        this.userSpace = new UserSpaceDefinition();
    }

    private final KernelConfigurationDefinition configuration;
    private final Map<UUID, ConnectionDefinition> connections;
    private final Map<String, UUID> namedConnectionIDs;
    private final UserSpaceDefinition userSpace;

    public Map<UUID, ConnectionDefinition> getConnections() {
        return this.connections;
    }

    public Map<String, UUID> getNamedConnectionIDs() {
        return this.namedConnectionIDs;
    }

    public KernelConfigurationDefinition getConfiguration() {
        return configuration;
    }

    public UserSpaceDefinition getUserSpace() {
        return this.userSpace;
    }
}
