package indi.sly.system.kernel.processes.lang;

import indi.sly.system.common.lang.Function1;
import indi.sly.system.kernel.processes.values.ProcessEntity;

import java.util.UUID;

@FunctionalInterface
public interface ProcessSelfFunction extends Function1<ProcessEntity, UUID> {
}
