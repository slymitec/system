package indi.sly.system.kernel.processes.lang;

import indi.sly.system.common.lang.Function2;
import indi.sly.system.kernel.processes.values.ProcessEntity;

@FunctionalInterface
public interface ProcessProcessorReadComponentFunction extends Function2<byte[], byte[], ProcessEntity> {
}
