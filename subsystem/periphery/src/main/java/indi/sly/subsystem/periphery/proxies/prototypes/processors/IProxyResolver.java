package indi.sly.subsystem.periphery.proxies.prototypes.processors;

import indi.sly.subsystem.periphery.core.prototypes.processors.IOrderlyResolver;
import indi.sly.subsystem.periphery.proxies.prototypes.mediators.ProxyProcessorMediator;
import indi.sly.subsystem.periphery.proxies.values.ProxyCacheEntity;

public interface IProxyResolver extends IOrderlyResolver {
    void resolve(ProxyCacheEntity proxy, ProxyProcessorMediator processorMediator);
}
