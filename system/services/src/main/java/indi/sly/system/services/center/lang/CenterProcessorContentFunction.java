package indi.sly.system.services.center.lang;

import indi.sly.system.common.lang.Function3;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import indi.sly.system.services.center.values.CenterDefinition;
import indi.sly.system.services.center.values.CenterStatusDefinition;

@FunctionalInterface
public interface CenterProcessorContentFunction extends Function3<ThreadContextObject, CenterDefinition, CenterStatusDefinition,
        ThreadContextObject> {
}
