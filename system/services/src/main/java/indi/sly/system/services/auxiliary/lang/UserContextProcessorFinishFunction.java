package indi.sly.system.services.auxiliary.lang;

import indi.sly.system.common.lang.Function1;
import indi.sly.system.services.auxiliary.prototypes.UserContextObject;

@FunctionalInterface
public interface UserContextProcessorFinishFunction extends Function1<UserContextObject, UserContextObject> {
}
