package indi.sly.system.kernel.processes.lang;

import indi.sly.system.common.lang.Function2;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;

@FunctionalInterface
public interface ProcessLifeProcessorEndFunction extends Function2<ProcessObject, ProcessObject, ProcessObject> {
}
