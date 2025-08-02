package indi.sly.subsystem.periphery.calls.prototypes.processors;

import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorConnectConsumer;
import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorDisconnectConsumer;
import indi.sly.subsystem.periphery.calls.prototypes.wrappers.ConnectionProcessorMediator;
import indi.sly.subsystem.periphery.calls.values.ConnectStatusRuntimeType;
import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectionStatusRuntimeResolver extends AConnectionResolver {
    public ConnectionStatusRuntimeResolver() {
        this.connect = (connection, status) -> status.setRuntime(ConnectStatusRuntimeType.CONNECTED);

        this.disconnect = (connection, status) -> status.setRuntime(ConnectStatusRuntimeType.DISCONNECTED);
    }

    @Override
    public int order() {
        return 2;
    }

    private final ConnectionProcessorConnectConsumer connect;
    private final ConnectionProcessorDisconnectConsumer disconnect;

    @Override
    public void resolve(ConnectionDefinition connection, ConnectionProcessorMediator processorMediator) {
        processorMediator.getConnects().add(this.connect);
        processorMediator.getDisconnects().add(this.disconnect);
    }
}
