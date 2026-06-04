package indi.sly.subsystem.periphery.calls.instances.prototypes.processors;

import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.subsystem.periphery.calls.values.ConnectionStatusDefinition;
import indi.sly.subsystem.periphery.calls.values.ClientResponseDefinition;
import indi.sly.subsystem.periphery.calls.values.ClientRequestDefinition;
import indi.sly.subsystem.periphery.core.prototypes.processors.AInitializer;

public abstract class AConnectionInitializer extends AInitializer {
    public abstract void connect(ConnectionDefinition connection, ConnectionStatusDefinition status);

    public abstract void disconnect(ConnectionDefinition connection, ConnectionStatusDefinition status);

    public abstract ClientResponseDefinition call(ClientRequestDefinition userContextRequest, ConnectionStatusDefinition status);
}
