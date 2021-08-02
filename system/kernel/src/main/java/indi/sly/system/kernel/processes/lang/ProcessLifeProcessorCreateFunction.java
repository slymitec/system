package indi.sly.system.kernel.processes.lang;

import indi.sly.system.common.lang.Function3;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.values.ProcessCreatorDefinition;

@FunctionalInterface
public interface ProcessLifeProcessorCreateFunction extends Function3<ProcessObject, ProcessObject, ProcessObject,
        ProcessCreatorDefinition> {
}
