package indi.sly.system.kernel.security.values;

import indi.sly.system.kernel.security.lang.PermissionCustomPredicate;

public record PermissionQueryRecord(boolean privilege, boolean role, PermissionCustomPredicate customDenyFunc) {
    public PermissionQueryRecord() {
        this(true, true, null);
    }
}
