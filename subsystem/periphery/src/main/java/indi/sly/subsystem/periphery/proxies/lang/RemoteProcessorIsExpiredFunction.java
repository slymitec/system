package indi.sly.subsystem.periphery.proxies.lang;

import indi.sly.subsystem.periphery.proxies.prototypes.ProcedureObject;
import indi.sly.subsystem.periphery.proxies.values.RemoteDefinition;
import indi.sly.system.common.lang.Function3;

@FunctionalInterface
public interface RemoteProcessorIsExpiredFunction extends Function3<Boolean, Boolean, RemoteDefinition, ProcedureObject> {
}
