package indi.sly.system.services.jobs.lang;

import indi.sly.system.common.lang.Function2;
import indi.sly.system.services.jobs.values.CallContextDefinition;
import indi.sly.system.services.jobs.values.CallContextRecord;

@FunctionalInterface
public interface CallContextProcessorCreateFunction extends Function2<CallContextDefinition, CallContextDefinition,
        CallContextRecord> {
}
