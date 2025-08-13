package indi.sly.subsystem.periphery.calls.prototypes.processors;

import indi.sly.subsystem.periphery.calls.instances.prototypes.processors.AConnectionInitializer;
import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorConnectConsumer;
import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorDisconnectConsumer;
import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorCallFunction;
import indi.sly.subsystem.periphery.calls.prototypes.wrappers.ConnectionProcessorMediator;
import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.system.common.supports.ObjectUtil;
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

        this.call = (connection, status, userContextRequest, userContentResponse) -> {
            if (ObjectUtil.allNotNull(userContentResponse)) {
                return userContentResponse;
            }

            AConnectionInitializer initializer = connection.getInitializer();
            initializer.send(userContextRequest, status);
            userContentResponse = initializer.receive(userContextRequest, status);

            return userContentResponse;
        };
    }

    @Override
    public int order() {
        return 1;
    }

    private final ConnectionProcessorConnectConsumer connect;
    private final ConnectionProcessorDisconnectConsumer disconnect;
    private final ConnectionProcessorCallFunction call;

    @Override
    public void resolve(ConnectionDefinition connection, ConnectionProcessorMediator processorMediator) {
        processorMediator.getConnects().add(this.connect);
        processorMediator.getDisconnects().add(this.disconnect);
        processorMediator.getCalls().add(this.call);
    }
}
