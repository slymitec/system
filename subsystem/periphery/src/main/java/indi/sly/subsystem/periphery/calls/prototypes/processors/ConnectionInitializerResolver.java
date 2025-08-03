package indi.sly.subsystem.periphery.calls.prototypes.processors;

import indi.sly.subsystem.periphery.calls.instances.prototypes.processors.AConnectionInitializer;
import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorConnectConsumer;
import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorDisconnectConsumer;
import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorSendFunction;
import indi.sly.subsystem.periphery.calls.prototypes.wrappers.ConnectionProcessorMediator;
import indi.sly.subsystem.periphery.calls.values.ConnectStatusRuntimeType;
import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.LogicalUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectionInitializerResolver extends AConnectionResolver {
    public ConnectionInitializerResolver() {
        this.connect = (connection, status) -> {
            AConnectionInitializer initializer = connection.getInitializer();

            initializer.connect(connection, status);

        };

        this.disconnect = (connection, status) -> {
            AConnectionInitializer initializer = connection.getInitializer();

            initializer.disconnect(connection, status);
        };

        this.send = (connection, status, userContextRequestRaw, userContentResponse) -> {
            AConnectionInitializer initializer = connection.getInitializer();

            return initializer.send(userContextRequestRaw, status);
        };
    }

    @Override
    public int order() {
        return 1;
    }

    private final ConnectionProcessorConnectConsumer connect;
    private final ConnectionProcessorDisconnectConsumer disconnect;
    private final ConnectionProcessorSendFunction send;

    @Override
    public void resolve(ConnectionDefinition connection, ConnectionProcessorMediator processorMediator) {
        processorMediator.getConnects().add(this.connect);
        processorMediator.getDisconnects().add(this.disconnect);
        processorMediator.getSends().add(this.send);
    }
}
