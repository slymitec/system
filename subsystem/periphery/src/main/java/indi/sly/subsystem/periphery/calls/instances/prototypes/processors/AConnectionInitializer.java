package indi.sly.subsystem.periphery.calls.instances.prototypes.processors;

import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.subsystem.periphery.calls.values.ConnectionStatusDefinition;
import indi.sly.subsystem.periphery.calls.values.UserContentResponseDefinition;
import indi.sly.subsystem.periphery.calls.values.UserContextRequestDefinition;
import indi.sly.subsystem.periphery.core.prototypes.processors.AInitializer;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AConnectionInitializer extends AInitializer {
    public AConnectionInitializer() {
    }

    public abstract void connect(ConnectionDefinition connection, ConnectionStatusDefinition status);

    public abstract void disconnect(ConnectionDefinition connection, ConnectionStatusDefinition status);

    public abstract UserContentResponseDefinition call(UserContextRequestDefinition userContextRequest, ConnectionStatusDefinition status);
}
