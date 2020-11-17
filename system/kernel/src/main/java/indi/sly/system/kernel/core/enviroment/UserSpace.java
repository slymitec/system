package indi.sly.system.kernel.core.enviroment;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import indi.sly.system.kernel.memory.caches.prototypes.InfoObjectCacheDefinition;
import indi.sly.system.kernel.objects.prototypes.InfoObject;

public class UserSpace {
    public UserSpace() {
        this.infoObjectLock = new ReentrantReadWriteLock();
        this.cachedInfoObjectDefinitions = new ConcurrentHashMap<>();
        this.infoObjects = new ConcurrentHashMap<>();
    }

    private final ReadWriteLock infoObjectLock;
    private final Map<UUID, InfoObjectCacheDefinition> cachedInfoObjectDefinitions;
    private final Map<UUID, InfoObject> infoObjects;

    public ReadWriteLock getInfoObjectLock() {
        return this.infoObjectLock;
    }

    public Map<UUID, InfoObjectCacheDefinition> getCachedInfoObjectDefinitions() {
        return cachedInfoObjectDefinitions;
    }

    public Map<UUID, InfoObject> getInfoObjects() {
        return infoObjects;
    }
}
