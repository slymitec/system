package indi.sly.subsystem.periphery.proxies.prototypes.processors;

import indi.sly.subsystem.periphery.core.prototypes.processors.IOrderlyResolver;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.RemoteProcessorMediator;
import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;

public interface IRemoteResolver extends IOrderlyResolver {
    void resolve(RemoteDefinition remote, RemoteProcessorMediator processorMediator);
}
