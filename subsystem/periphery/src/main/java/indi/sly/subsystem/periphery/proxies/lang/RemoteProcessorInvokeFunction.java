package indi.sly.subsystem.periphery.proxies.lang;

import indi.sly.subsystem.periphery.proxies.prototypes.AProxyObject;
import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;
import indi.sly.system.common.lang.Function5;

@FunctionalInterface
public interface RemoteProcessorInvokeFunction extends Function5<RemoteDefinition, RemoteDefinition, RemoteDefinition, Class<? extends AProxyObject>, String, Object[]> {
}
