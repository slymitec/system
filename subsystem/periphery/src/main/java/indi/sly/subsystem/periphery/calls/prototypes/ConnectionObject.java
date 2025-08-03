package indi.sly.subsystem.periphery.calls.prototypes;

import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorConnectConsumer;
import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorDisconnectConsumer;
import indi.sly.subsystem.periphery.calls.lang.ConnectionProcessorSendFunction;
import indi.sly.subsystem.periphery.calls.prototypes.wrappers.ConnectionProcessorMediator;
import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.subsystem.periphery.calls.values.ConnectionStatusDefinition;
import indi.sly.subsystem.periphery.calls.values.UserContentResponseDefinition;
import indi.sly.subsystem.periphery.calls.values.UserContextRequestRawDefinition;
import indi.sly.subsystem.periphery.core.prototypes.AIndependentValueProcessObject;
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
public class ConnectionObject extends AIndependentValueProcessObject<ConnectionDefinition> {
    protected ConnectionProcessorMediator processorMediator;
    protected ConnectionStatusDefinition status;

    public UUID getID() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getID();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public long getRuntime() {
        return this.status.getRuntime();
    }

    public void connect() {
        List<ConnectionProcessorConnectConsumer> resolvers = this.processorMediator.getConnects();

        try {
            this.lock(LockType.READ);
            this.init();

            for (ConnectionProcessorConnectConsumer resolver : resolvers) {
                resolver.accept(this.value, this.status);
            }
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void disconnect() {
        List<ConnectionProcessorDisconnectConsumer> resolvers = this.processorMediator.getDisconnects();

        try {
            this.lock(LockType.READ);
            this.init();

            for (ConnectionProcessorDisconnectConsumer resolver : resolvers) {
                resolver.accept(this.value, this.status);
            }
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public synchronized UserContentResponseDefinition send(UserContextRequestRawDefinition userContextRequestRaw) {
        if (ObjectUtil.isAnyNull(userContextRequestRaw)) {
            throw new ConditionParametersException();
        }

        UserContentResponseDefinition userContentResponse = null;

        List<ConnectionProcessorSendFunction> resolvers = this.processorMediator.getSends();

        try {
            this.lock(LockType.READ);
            this.init();

            for (ConnectionProcessorSendFunction resolver : resolvers) {
                userContentResponse = resolver.apply(this.value, this.status, userContextRequestRaw, userContentResponse);
            }
        } finally {
            this.lock(LockType.NONE);
        }

        return userContentResponse;
    }
}
