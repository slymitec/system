package indi.sly.subsystem.periphery.calls.prototypes.processors;

import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorConnectConsumer;
import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorDisconnectConsumer;
import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorCallFunction;
import indi.sly.subsystem.periphery.calls.prototypes.wrappers.ConnectionProcessorMediator;
import indi.sly.subsystem.periphery.calls.values.ConnectStatusRuntimeType;
import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ValueUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectionCheckConditionResolver extends AConnectionResolver {
    public ConnectionCheckConditionResolver() {
        this.connect = (connection, status) -> {
            if (LogicalUtil.allNotEqual(status.getRuntime(), ConnectStatusRuntimeType.INITIALIZATION, ConnectStatusRuntimeType.DISCONNECTED)) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.disconnect = (connection, status) -> {
            if (LogicalUtil.allNotEqual(status.getRuntime(), ConnectStatusRuntimeType.CONNECTED)) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.call = (connection, status, userContextRequest, userContentResponse) -> {
            if (LogicalUtil.allNotEqual(status.getRuntime(), ConnectStatusRuntimeType.CONNECTED)) {
                throw new StatusRelationshipErrorException();
            }
            if (ValueUtil.isAnyNullOrEmpty(userContextRequest.getContent().getID())) {
                throw new ConditionParametersException();
            }

            return userContentResponse;
        };
    }

    @Override
    public int order() {
        return 0;
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
