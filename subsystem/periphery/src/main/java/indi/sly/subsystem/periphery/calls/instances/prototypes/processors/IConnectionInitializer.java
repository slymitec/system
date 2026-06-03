package indi.sly.subsystem.periphery.calls.instances.prototypes.processors;

import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.subsystem.periphery.calls.values.ConnectionStatusDefinition;
import indi.sly.subsystem.periphery.calls.values.ClientResponseDefinition;
import indi.sly.subsystem.periphery.calls.values.ClientRequestDefinition;
import indi.sly.subsystem.periphery.core.prototypes.processors.AInitializer;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

public interface IConnectionInitializer {
    void connect(ConnectionDefinition connection, ConnectionStatusDefinition status);

    void disconnect(ConnectionDefinition connection, ConnectionStatusDefinition status);

    ClientResponseDefinition call(ClientRequestDefinition userContextRequest, ConnectionStatusDefinition status);
}
