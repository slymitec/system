package indi.sly.system.services.job.lang;

import indi.sly.system.common.lang.Function1;
import indi.sly.system.services.job.prototypes.UserContextObject;

@FunctionalInterface
public interface UserContextProcessorFinishFunction extends Function1<UserContextObject, UserContextObject> {
}
