package indi.sly.subsystem.periphery.proxies.lang;

import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;
import indi.sly.system.common.lang.Function4;

@FunctionalInterface
public interface RemoteProcessorInvokeFunction extends Function4<RemoteDefinition, RemoteDefinition, RemoteDefinition, String, Object[]> {
}
