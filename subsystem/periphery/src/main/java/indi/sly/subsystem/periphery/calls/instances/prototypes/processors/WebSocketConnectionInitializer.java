package indi.sly.subsystem.periphery.calls.instances.prototypes.processors;

import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.subsystem.periphery.calls.values.ConnectionStatusDefinition;
import indi.sly.subsystem.periphery.calls.values.UserContextRequestRawDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WebSocketConnectionInitializer extends AConnectionInitializer {
    @Override
    public void connect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
    }

    @Override
    public void disconnect(ConnectionDefinition connection, ConnectionStatusDefinition status) {
    }

    @Override
    public Object send(UserContextRequestRawDefinition userContextRequestRawm, ConnectionStatusDefinition status) {
        return null;
    }
}
