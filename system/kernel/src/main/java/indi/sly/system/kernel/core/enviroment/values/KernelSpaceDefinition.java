package indi.sly.system.kernel.core.enviroment.values;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.inject.Named;
import javax.inject.Singleton;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.core.values.ADefinition;
import indi.sly.system.kernel.memory.caches.values.InfoCacheDefinition;

@Named
@Singleton
public class KernelSpaceDefinition extends ADefinition<KernelSpaceDefinition> {
    public KernelSpaceDefinition() {
        this.configuration = new KernelConfigurationDefinition();
        this.corePrototypeLock = new ReentrantReadWriteLock();
        this.corePrototypes = new ConcurrentHashMap<>();
        this.namedCorePrototypeIDs = new ConcurrentHashMap<>();
        this.classedCorePrototypeIDs = new ConcurrentHashMap<>();
        this.prototypeTypes = new ConcurrentSkipListSet<>();
        this.infoCaches = new ConcurrentHashMap<>();
    }

    private final KernelConfigurationDefinition configuration;
    private final ReadWriteLock corePrototypeLock;
    private final Map<UUID, APrototype> corePrototypes;
    private final Map<String, UUID> namedCorePrototypeIDs;
    private final Map<Class<? extends APrototype>, UUID> classedCorePrototypeIDs;
    private final Set<UUID> prototypeTypes;
    private final Map<UUID, InfoCacheDefinition> infoCaches;

    public KernelConfigurationDefinition getConfiguration() {
        return configuration;
    }

    public ReadWriteLock getCorePrototypeLock() {
        return this.corePrototypeLock;
    }

    public Map<UUID, APrototype> getCorePrototypes() {
        return this.corePrototypes;
    }

    public Map<String, UUID> getNamedCorePrototypeIDs() {
        return this.namedCorePrototypeIDs;
    }

    public Map<Class<? extends APrototype>, UUID> getClassedCorePrototypeIDs() {
        return classedCorePrototypeIDs;
    }

    public Set<UUID> getPrototypeTypes() {
        return this.prototypeTypes;
    }

    public Map<UUID, InfoCacheDefinition> getInfoCaches() {
        return this.infoCaches;
    }
}
