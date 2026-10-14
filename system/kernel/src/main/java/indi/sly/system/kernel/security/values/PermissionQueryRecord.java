package indi.sly.system.kernel.security.values;

import indi.sly.system.kernel.security.lang.PermissionCustomPredicate;
import org.jspecify.annotations.Nullable;

public record PermissionQueryRecord(boolean privilege, boolean role,
                                    @Nullable PermissionCustomPredicate customDenyFunc) {
    public PermissionQueryRecord() {
        this(true, true, null);
    }
}
