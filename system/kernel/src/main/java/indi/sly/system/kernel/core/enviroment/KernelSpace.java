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

import indi.sly.system.kernel.core.prototypes.ACorePrototype;
import indi.sly.system.kernel.memory.caches.values.InfoCacheDefinition;

@Named
@Singleton
public class KernelSpace {
    public KernelSpace() {
        this.configuration = new KernelConfiguration();
        this.corePrototypeLock = new ReentrantReadWriteLock();
        this.corePrototypes = new ConcurrentHashMap<>();
        this.namedCorePrototypeIDs = new ConcurrentHashMap<>();
        this.classedCorePrototypeIDs = new ConcurrentHashMap<>();
        this.prototypeTypes = new ConcurrentSkipListSet<>();
        this.infoCaches = new ConcurrentHashMap<>();
    }

    private final KernelConfiguration configuration;
    private final ReadWriteLock corePrototypeLock;
    private final Map<UUID, ACorePrototype> corePrototypes;
    private final Map<String, UUID> namedCorePrototypeIDs;
    private final Map<Class<? extends ACorePrototype>, UUID> classedCorePrototypeIDs;
    private final Set<UUID> prototypeTypes;
    private final Map<UUID, InfoCacheDefinition> infoCaches;

    public KernelConfiguration getConfiguration() {
        return configuration;
    }

    public ReadWriteLock getCorePrototypeLock() {
        return this.corePrototypeLock;
    }

    public Map<UUID, ACorePrototype> getCorePrototypes() {
        return this.corePrototypes;
    }

    public Map<String, UUID> getNamedCorePrototypeIDs() {
        return this.namedCorePrototypeIDs;
    }

    public Map<Class<? extends ACorePrototype>, UUID> getClassedCorePrototypeIDs() {
        return classedCorePrototypeIDs;
    }

    public Set<UUID> getPrototypeTypes() {
        return this.prototypeTypes;
    }

    public Map<UUID, InfoCacheDefinition> getInfoCaches() {
        return this.infoCaches;
    }
}
