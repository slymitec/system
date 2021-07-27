package indi.sly.system.kernel.processes.lang;

import indi.sly.system.common.lang.Consumer2;
import indi.sly.system.kernel.processes.values.ProcessEntity;

@FunctionalInterface
public interface WriteProcessStatusConsumer extends Consumer2<ProcessEntity, Long> {
}
