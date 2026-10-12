package indi.sly.system.kernel.processes.lang;

import indi.sly.system.common.lang.Function2;
import indi.sly.system.kernel.processes.values.ProcessEntity;

import java.util.UUID;

@FunctionalInterface
public interface ProcessProcessorSelfFunction extends Function2<ProcessEntity, UUID, Long> {
}
