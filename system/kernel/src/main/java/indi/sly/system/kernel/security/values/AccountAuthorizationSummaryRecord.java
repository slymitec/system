package indi.sly.system.kernel.security.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;

import java.util.*;

public record AccountAuthorizationSummaryRecord(UUID id, String name, String password,
                                                AccountAuthorizationTokenRecord token, Set<UUID> sessions) {
    public AccountAuthorizationSummaryRecord {
        if (ObjectUtil.isAnyNull(sessions)) {
            throw new ConditionParametersException();
        }
    }

    @Override
    public Set<UUID> sessions() {
        return CollectionUtil.unmodifiable(this.sessions);
    }
}
