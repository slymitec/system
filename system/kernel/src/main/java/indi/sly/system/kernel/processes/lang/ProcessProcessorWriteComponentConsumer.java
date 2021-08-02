package indi.sly.system.kernel.processes.lang;

import indi.sly.system.common.lang.Consumer2;
import indi.sly.system.kernel.processes.values.ProcessEntity;

@FunctionalInterface
public interface ProcessProcessorWriteComponentConsumer extends Consumer2<ProcessEntity, byte[]> {
}
