package indi.sly.system.services.jobs.values;

import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;

import java.util.*;

public record ClientRequestRecord(String method, List<String> parameters) {
    public ClientRequestRecord {
        if (ObjectUtil.isAnyNull(parameters)) {
            parameters = new ArrayList<>();
        }
    }

    public List<String> parameters() {
        return CollectionUtil.unmodifiable(this.parameters);
    }
}
