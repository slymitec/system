package indi.sly.system.kernel.core.enviroment.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.core.values.HandleEntryDefinition;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Named
public abstract class ASpaceDefinition<T> extends ADefinition<T> {
    public ASpaceDefinition() {
        this.coreObjects = new ConcurrentHashMap<>();
        this.handledHandles = new ConcurrentHashMap<>();
        this.classedHandles = new ConcurrentHashMap<>();
        this.coreObjectLock = new ReentrantReadWriteLock();
        this.coreObjectLimit = 0L;

        this.corePrototypes = new ConcurrentHashMap<>();
        this.namedCorePrototypeIDs = new ConcurrentHashMap<>();
        this.classedCorePrototypeIDs = new ConcurrentHashMap<>();
        this.corePrototypeLock = new ReentrantReadWriteLock();
    }

    private final Map<UUID, AObject> coreObjects;
    private final Map<UUID, HandleEntryDefinition> handledHandles;
    private final Map<Class<? extends AObject>, HandleEntryDefinition> classedHandles;
    private final ReadWriteLock coreObjectLock;
    private long coreObjectLimit;

    public Map<UUID, AObject> getCoreObjects() {
        return this.coreObjects;
    }

    public Map<UUID, HandleEntryDefinition> getHandledHandles() {
        return this.handledHandles;
    }

    public Map<Class<? extends AObject>, HandleEntryDefinition> getClassedHandles() {
        return this.classedHandles;
    }

    public ReadWriteLock getCoreObjectLock() {
        return this.coreObjectLock;
    }

    public long getCoreObjectLimit() {
        return this.coreObjectLimit;
    }

    public void setCoreObjectLimit(long coreObjectLimit) {
        this.coreObjectLimit = coreObjectLimit;
    }

    //

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
