package indi.sly.subsystem.periphery.proxies.values;

import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.ADefinition;

import java.util.*;

public class HandleTableDefinition extends ADefinition {
    public HandleTableDefinition() {
        this.handles = new HashMap<>();
    }

    private final Map<UUID, HandleEntryDefinition> handles;

    public Set<UUID> list() {
        return this.handles.keySet();
    }

    public HandleEntryDefinition get(UUID handle) {
        HandleEntryDefinition handleEntry = this.handles.getOrDefault(handle, null);

        if (ObjectUtil.isAnyNull(handleEntry)) {
            throw new StatusNotExistedException();
        }

        return handleEntry;
    }

    public void add(HandleEntryDefinition handleEntry) {
        if (this.handles.containsKey(handleEntry.getHandle())) {
            throw new StatusNotExistedException();
        }

        this.handles.put(handleEntry.getHandle(), handleEntry);
    }

    public void delete(UUID handle) {
        HandleEntryDefinition handleEntry = this.handles.remove(handle);

        if (ObjectUtil.isAnyNull(handleEntry)) {
            throw new StatusNotExistedException();
        }
    }
}
