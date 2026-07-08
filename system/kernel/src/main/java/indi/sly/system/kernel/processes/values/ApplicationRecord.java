package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;

import java.util.Map;
import java.util.UUID;

public record ApplicationRecord(UUID id, String name, String serverURL, Map<String, String> configurations) {
    public ApplicationRecord {
        if (ObjectUtil.isAnyNull(configurations)) {
            throw new ConditionParametersException();
        }
    }
}

