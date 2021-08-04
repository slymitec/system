package indi.sly.system.kernel.security.values;

import indi.sly.system.common.lang.Provider;
import indi.sly.system.kernel.security.prototypes.AccountObject;

@FunctionalInterface
public interface AccountAuthorizationGetAccount extends Provider<AccountObject> {
}
