package indi.sly.system.services.jobs.lang;

import indi.sly.system.common.lang.Function1;
import indi.sly.system.services.jobs.prototypes.CallContextObject;

@FunctionalInterface
public interface CallContextProcessorEndFunction extends Function1<CallContextObject, CallContextObject> {
}
