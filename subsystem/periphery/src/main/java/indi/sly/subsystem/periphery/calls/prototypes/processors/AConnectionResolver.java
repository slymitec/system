package indi.sly.subsystem.periphery.calls.prototypes.processors;

import indi.sly.subsystem.periphery.calls.prototypes.wrappers.ConnectionProcessorMediator;
import indi.sly.subsystem.periphery.calls.values.ConnectionDefinition;
import indi.sly.subsystem.periphery.core.prototypes.processors.AResolver;

public abstract class AConnectionResolver extends AResolver {
    public abstract void resolve(ConnectionDefinition connection, ConnectionProcessorMediator processorMediator);
}
