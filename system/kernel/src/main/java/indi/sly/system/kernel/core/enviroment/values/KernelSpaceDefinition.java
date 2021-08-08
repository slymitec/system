package indi.sly.system.kernel.core.enviroment.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.kernel.core.prototypes.APrototype;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Named
@Singleton
public class KernelSpaceDefinition extends ADefinition<KernelSpaceDefinition> {
    public KernelSpaceDefinition() {
        this.configuration = new KernelConfigurationDefinition();
        this.corePrototypeLock = new ReentrantReadWriteLock();
        this.corePrototypes = new ConcurrentHashMap<>();
        this.namedCorePrototypeIDs = new ConcurrentHashMap<>();
        this.classedCorePrototypeIDs = new ConcurrentHashMap<>();
        this.infoTypeIDs = new ConcurrentSkipListSet<>();
    }

    private final KernelConfigurationDefinition configuration;
    private final ReadWriteLock corePrototypeLock;
    private final Map<UUID, APrototype> corePrototypes;
    private final Map<String, UUID> namedCorePrototypeIDs;
    private final Map<Class<? extends APrototype>, UUID> classedCorePrototypeIDs;
    private final Set<UUID> infoTypeIDs;

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

    public Set<UUID> getInfoTypeIDs() {
        return this.infoTypeIDs;
    }
}
