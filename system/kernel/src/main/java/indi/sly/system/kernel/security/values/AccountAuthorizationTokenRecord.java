package indi.sly.system.kernel.security.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;

import java.util.*;

public record AccountAuthorizationTokenRecord(long privileges, Map<Long, Integer> limits, Set<UUID> roles) {
    public AccountAuthorizationTokenRecord {
        if (ObjectUtil.isAnyNull(limits, roles)) {
            throw new ConditionParametersException();
        }
    }

    @Override
    public Map<Long, Integer> limits() {
        return CollectionUtil.unmodifiable(this.limits);
    }

    @Override
    public Set<UUID> roles() {
        return CollectionUtil.unmodifiable(this.roles);
    }
}
