package indi.sly.subsystem.periphery.calls.prototypes;

import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorConnectConsumer;
import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorDisconnectConsumer;
import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorCallFunction;
import indi.sly.subsystem.periphery.calls.prototypes.wrappers.ConnectionProcessorMediator;
import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.subsystem.periphery.calls.values.ConnectionStatusDefinition;
import indi.sly.subsystem.periphery.calls.values.ClientResponseDefinition;
import indi.sly.subsystem.periphery.calls.values.ClientRequestDefinition;
import indi.sly.subsystem.periphery.core.prototypes.ADefinitionObject;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConnectionObject extends ADefinitionObject<ConnectionDefinition> {
    protected ConnectionProcessorMediator processorMediator;
    protected ConnectionStatusDefinition status;

    public UUID getId() {
        return this.definition.getId();
    }

    public long getRuntime() {
        return this.status.getRuntime();
    }

    public void connect() {
        List<ConnectionProcessorConnectConsumer> resolvers = this.processorMediator.getConnects();

        for (ConnectionProcessorConnectConsumer resolver : resolvers) {
            resolver.accept(this.definition, this.status);
        }
    }

    public void disconnect() {
        List<ConnectionProcessorDisconnectConsumer> resolvers = this.processorMediator.getDisconnects();

        for (ConnectionProcessorDisconnectConsumer resolver : resolvers) {
            resolver.accept(this.definition, this.status);
        }
    }

    public ClientResponseDefinition call(ClientRequestDefinition userContextRequest) {
        if (ObjectUtil.isAnyNull(userContextRequest)) {
            throw new ConditionParametersException();
        }

        ClientResponseDefinition userContentResponse = null;

        List<ConnectionProcessorCallFunction> resolvers = this.processorMediator.getCalls();

        for (ConnectionProcessorCallFunction resolver : resolvers) {
            userContentResponse = resolver.apply(this.definition, this.status, userContextRequest, userContentResponse);
        }

        return userContentResponse;
    }
}
