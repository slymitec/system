package indi.sly.subsystem.periphery.calls.prototypes.processors;

import indi.sly.subsystem.periphery.calls.prototypes.wrappers.ConnectionProcessorMediator;
import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.subsystem.periphery.core.prototypes.processors.AResolver;
import indi.sly.subsystem.periphery.core.prototypes.processors.IOrderlyResolver;

public interface IConnectionResolver extends IOrderlyResolver {
    public void resolve(ConnectionDefinition connection, ConnectionProcessorMediator processorMediator);
}
