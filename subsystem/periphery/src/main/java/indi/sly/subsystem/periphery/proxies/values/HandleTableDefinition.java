package indi.sly.subsystem.periphery.proxies.values;

import indi.sly.subsystem.periphery.proxies.prototypes.RemoteObject;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.ADefinition;

import java.util.*;

public class HandleTableDefinition extends ADefinition {
    public HandleTableDefinition() {
        this.handles = new HashMap<>();
    }

    private final Map<UUID, RemoteObject> handles;

    public Set<UUID> list() {
        return this.handles.keySet();
    }

    public RemoteObject get(UUID handle) {
        RemoteObject remote = this.handles.getOrDefault(handle, null);

        if (ObjectUtil.isAnyNull(remote)) {
            throw new StatusNotExistedException();
        }

        return remote;
    }

    public void add(RemoteObject remote) {
        UUID handle = ObjectUtil.transferFromString(UUID.class, remote.getRemoteValue());

        if (this.handles.containsKey(handle)) {
            throw new StatusNotExistedException();
        }

        this.handles.put(handle, remote);
    }

    public void delete(UUID handle) {
        RemoteObject remote = this.handles.remove(handle);

        if (ObjectUtil.isAnyNull(remote)) {
            throw new StatusNotExistedException();
        }
    }
}
