package indi.sly.subsystem.periphery.calls.prototypes.processors;

import indi.sly.subsystem.periphery.calls.instances.prototypes.processors.AConnectionInitializer;
import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorConnectConsumer;
import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorDisconnectConsumer;
import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorSendFunction;
import indi.sly.subsystem.periphery.calls.prototypes.wrappers.ConnectionProcessorMediator;
import indi.sly.subsystem.periphery.calls.values.ConnectStatusRuntimeType;
import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.subsystem.periphery.calls.values.UserContentRequestDefinition;
import indi.sly.subsystem.periphery.calls.values.UserContentResponseDefinition;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

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

        this.send = (connection, status, userContextRequest, userContentResponseFuture) -> {
            Map<UUID, UserContentRequestDefinition> requests = status.getRequest();
            Map<UUID, UserContentResponseDefinition> responses = status.getResponses();

            UserContentRequestDefinition userContentRequest = userContextRequest.getContent();
            UUID id = userContentRequest.getID();

            if (ObjectUtil.isAnyNull(userContentResponseFuture)) {
                userContentResponseFuture = status.getExecutor().submit(new Callable<>() {
                    private UserContentResponseDefinition userContentResponse;

                    @Override
                    public UserContentResponseDefinition call() throws Exception {
                        synchronized (userContentRequest) {
                            if (ObjectUtil.allNotNull(this.userContentResponse)) {
                                return this.userContentResponse;
                            }

                            if (responses.containsKey(id)) {
                                requests.remove(id);
                                userContentRequest.setID(UUIDUtil.getEmpty());
                                this.userContentResponse = responses.remove(id);

                                return this.userContentResponse;
                            }

                            if (ValueUtil.isAnyNullOrEmpty(userContentRequest.getID())) {
                                throw new StatusUnexpectedException();
                            }

                            userContentRequest.wait(8192);

                            requests.remove(id);
                            userContentRequest.setID(UUIDUtil.getEmpty());
                            if (responses.containsKey(id)) {
                                this.userContentResponse = responses.remove(id);

                                return this.userContentResponse;
                            }

                            throw new StatusUnexpectedException();
                        }
                    }
                });
            }

            requests.put(userContentRequest.getID(), userContentRequest);

            AConnectionInitializer initializer = connection.getInitializer();
            initializer.send(userContextRequest, status);

            return userContentResponseFuture;
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
