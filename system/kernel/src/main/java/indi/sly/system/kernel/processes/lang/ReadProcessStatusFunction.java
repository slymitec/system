package indi.sly.system.kernel.processes.lang;

import indi.sly.system.common.lang.Function2;
import indi.sly.system.kernel.processes.values.ProcessEntity;

@FunctionalInterface
public interface ReadProcessStatusFunction extends Function2<Long, Long, ProcessEntity> {
}
