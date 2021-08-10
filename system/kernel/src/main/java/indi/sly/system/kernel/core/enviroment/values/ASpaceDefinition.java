package indi.sly.system.kernel.core.enviroment.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.kernel.core.prototypes.APrototype;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Named
public abstract class ASpaceDefinition<T> extends ADefinition<T> {
    public ASpaceDefinition() {
        this.corePrototypes = new ConcurrentHashMap<>();
        this.namedCorePrototypeIDs = new ConcurrentHashMap<>();
        this.classedCorePrototypeIDs = new ConcurrentHashMap<>();
        this.corePrototypeLock = new ReentrantReadWriteLock();
    }

    private final Map<UUID, APrototype> corePrototypes;
    private final Map<String, UUID> namedCorePrototypeIDs;
    private final Map<Class<? extends APrototype>, UUID> classedCorePrototypeIDs;
    private final ReadWriteLock corePrototypeLock;

    public final Map<UUID, APrototype> getCorePrototypes() {
        return this.corePrototypes;
    }

    public final Map<String, UUID> getNamedCorePrototypeIDs() {
        return this.namedCorePrototypeIDs;
    }

    public final Map<Class<? extends APrototype>, UUID> getClassedCorePrototypeIDs() {
        return classedCorePrototypeIDs;
    }

    public final ReadWriteLock getCorePrototypeLock() {
        return this.corePrototypeLock;
    }
}
