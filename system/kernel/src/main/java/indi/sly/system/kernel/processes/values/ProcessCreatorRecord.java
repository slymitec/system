package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;

import java.util.UUID;

public record ProcessCreatorRecord(AccountAuthorizationObject accountAuthorization, boolean inheritSession,
                                   long contextType, UUID fileIndex, String parameters, PathRecord workFolder) {
}
