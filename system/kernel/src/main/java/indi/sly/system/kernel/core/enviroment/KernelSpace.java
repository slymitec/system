package indi.sly.system.kernel.core.enviroment;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.inject.Named;
import javax.inject.Singleton;

import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.memory.caches.values.InfoObjectCacheDefinition;

@Named
@Singleton
public class KernelSpace {
    public KernelSpace() {
        this.configuration = new KernelConfiguration();
        this.coreObjectLock = new ReentrantReadWriteLock();
        this.coreObjects = new ConcurrentHashMap<>();
        this.namedCoreObjectIDs = new ConcurrentHashMap<>();
        this.classedCoreObjectIDs = new ConcurrentHashMap<>();
        this.objectTypes = new ConcurrentSkipListSet<>();
        this.cachedInfoObjectDefinitions = new ConcurrentHashMap<>();
    }

    private final KernelConfiguration configuration;
    private final ReadWriteLock coreObjectLock;
    private final Map<UUID, ACoreObject> coreObjects;
    private final Map<String, UUID> namedCoreObjectIDs;
    private final Map<Class<? extends ACoreObject>, UUID> classedCoreObjectIDs;
    private final Set<UUID> objectTypes;
    private final Map<UUID, InfoObjectCacheDefinition> cachedInfoObjectDefinitions;

    public KernelConfiguration getConfiguration() {
        return configuration;
    }

    public ReadWriteLock getCoreObjectLock() {
        return this.coreObjectLock;
    }

    public Map<UUID, ACoreObject> getCoreObjects() {
        return this.coreObjects;
    }

    public Map<String, UUID> getNamedCoreObjectIDs() {
        return this.namedCoreObjectIDs;
    }

    public Map<Class<? extends ACoreObject>, UUID> getClassedCoreObjectIDs() {
        return classedCoreObjectIDs;
    }

    public Set<UUID> getObjectTypes() {
        return this.objectTypes;
    }

    public Map<UUID, InfoObjectCacheDefinition> getCachedInfoObjectDefinitions() {
        return this.cachedInfoObjectDefinitions;
    }
}
