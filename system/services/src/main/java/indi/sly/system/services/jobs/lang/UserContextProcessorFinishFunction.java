package indi.sly.system.services.jobs.lang;

import indi.sly.system.common.lang.Function1;
import indi.sly.system.services.jobs.prototypes.UserContextObject;

@FunctionalInterface
public interface UserContextProcessorFinishFunction extends Function1<UserContextObject, UserContextObject> {
}
