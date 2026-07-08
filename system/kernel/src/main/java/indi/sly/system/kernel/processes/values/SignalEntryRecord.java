package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;

import java.util.Map;
import java.util.UUID;

public record SignalEntryRecord(UUID source, long key, long value, Map<Long, Long> date) {
    public SignalEntryRecord {
        if (ObjectUtil.isAnyNull(date)) {
            throw new ConditionParametersException();
        }
    }

    @Override
    public Map<Long, Long> date() {
        return CollectionUtil.unmodifiable(this.date);
    }

}
