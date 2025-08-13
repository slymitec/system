package indi.sly.subsystem.periphery.calls.prototypes;

import indi.sly.subsystem.periphery.calls.prototypes.processors.AConnectionResolver;
import indi.sly.subsystem.periphery.calls.prototypes.processors.ConnectionCheckConditionResolver;
import indi.sly.subsystem.periphery.calls.prototypes.processors.ConnectionInitializerResolver;
import indi.sly.subsystem.periphery.calls.prototypes.processors.ConnectionStatusRuntimeResolver;
import indi.sly.subsystem.periphery.calls.prototypes.wrappers.ConnectionProcessorMediator;
import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.subsystem.periphery.calls.values.ConnectionStatusDefinition;
import indi.sly.subsystem.periphery.core.prototypes.AFactory;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.ObjectUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CallFactory extends AFactory {
    public CallFactory() {
        this.connectionResolvers = new ArrayList<>();
    }

    protected final List<AConnectionResolver> connectionResolvers;

    @Override
    public void init() {
        this.connectionResolvers.add(this.factoryManager.create(ConnectionCheckConditionResolver.class));
        this.connectionResolvers.add(this.factoryManager.create(ConnectionInitializerResolver.class));
        this.connectionResolvers.add(this.factoryManager.create(ConnectionStatusRuntimeResolver.class));

        Collections.sort(this.connectionResolvers);
    }

    private ConnectionObject buildConnection(ConnectionProcessorMediator processorMediator, Provider<ConnectionDefinition> funcRead,
                                             Consumer1<ConnectionDefinition> funcWrite) {
        ConnectionObject connection = this.factoryManager.create(ConnectionObject.class);

        connection.setSource(funcRead, funcWrite);
        connection.processorMediator = processorMediator;
        connection.status = new ConnectionStatusDefinition();
        connection.status.setConnection(connection);

        return connection;
    }

    public ConnectionObject buildConnection(ConnectionDefinition connection) {
        if (ObjectUtil.isAnyNull(connection)) {
            throw new ConditionParametersException();
        }

        ConnectionProcessorMediator processorMediator = this.factoryManager.create(ConnectionProcessorMediator.class);
        for (AConnectionResolver resolver : this.connectionResolvers) {
            resolver.resolve(connection, processorMediator);
        }

        return this.buildConnection(processorMediator, () -> connection, (source) -> {
        });
    }

    public ConnectionBuilder createConnection() {
        ConnectionBuilder connectionBuilder = this.factoryManager.create(ConnectionBuilder.class);

        connectionBuilder.factory = this;

        return connectionBuilder;
    }


}
