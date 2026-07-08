package indi.sly.system.services.jobs.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;

import java.util.List;
import java.util.UUID;

public record ClientResponseExceptionRecord(UUID id, String clazz, List<ClientResponseExceptionTraceRecord> trace) {
    public ClientResponseExceptionRecord {
        if (ObjectUtil.isAnyNull(trace)) {
            throw new ConditionParametersException();
        }
    }

    @Override
    public List<ClientResponseExceptionTraceRecord> trace() {
        return CollectionUtil.unmodifiable(this.trace);
    }
}
