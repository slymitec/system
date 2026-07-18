package indi.sly.subsystem.periphery.proxies.lang;

import indi.sly.subsystem.periphery.proxies.prototypes.ProcedureObject;
import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;
import indi.sly.system.common.lang.Consumer3;

@FunctionalInterface
public interface RemoteProcessorExpireConsumer extends Consumer3<RemoteDefinition, ProcedureObject, Long> {
}
