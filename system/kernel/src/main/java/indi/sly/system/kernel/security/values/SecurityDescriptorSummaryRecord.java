package indi.sly.system.kernel.security.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.PathRecord;

import java.util.*;

public record SecurityDescriptorSummaryRecord(PathRecord path, boolean inherit, boolean permission, boolean audit,
                                              Set<AccessControlRecord> permissions,
                                              Set<AccessControlRecord> audits) {
    public SecurityDescriptorSummaryRecord {
        if (ObjectUtil.isAnyNull(permission, audits)) {
            throw new ConditionParametersException();
        }
    }

    @Override
    public Set<AccessControlRecord> permissions() {
        return CollectionUtil.unmodifiable(this.permissions);
    }

    @Override
    public Set<AccessControlRecord> audits() {
        return CollectionUtil.unmodifiable(this.audits);
    }
}
