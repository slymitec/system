package indi.sly.subsystem.periphery.proxies.lang;

import indi.sly.subsystem.periphery.calls.values.ClientResponseDefinition;
import indi.sly.subsystem.periphery.proxies.values.ProxyCacheEntity;
import indi.sly.system.common.lang.Function4;

@FunctionalInterface
public interface ProxyInvokeFunction extends Function4<ClientResponseDefinition, ClientResponseDefinition, ProxyCacheEntity, String, Object[]> {
}
