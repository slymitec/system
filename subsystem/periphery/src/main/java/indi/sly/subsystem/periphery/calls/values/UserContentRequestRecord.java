package indi.sly.subsystem.periphery.calls.values;

import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;

import java.util.*;

public record UserContentRequestRecord(UUID id, String task, String method, List<String> parameters) {
    public UserContentRequestRecord {
        if (ObjectUtil.isAnyNull(parameters)) {
            parameters = new ArrayList<>();
        }
    }

    public List<String> parameters() {
        return CollectionUtil.unmodifiable(this.parameters);
    }
}
