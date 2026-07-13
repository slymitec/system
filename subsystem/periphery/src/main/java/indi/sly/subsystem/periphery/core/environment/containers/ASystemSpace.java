package indi.sly.subsystem.periphery.core.environment.containers;

import indi.sly.subsystem.periphery.core.prototypes.AObject;
import indi.sly.system.common.containers.ASpace;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.values.LockType;
import jakarta.inject.Named;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Named
public abstract class ASystemSpace extends ASpace {
    public ASystemSpace() {
        this.objects = new ConcurrentHashMap<>();
        this.classedObjects = new ConcurrentHashMap<>();

        ReentrantReadWriteLock objectLock = new ReentrantReadWriteLock();
        this.objectReadLock = objectLock.readLock();
        this.objectWriteLock = objectLock.writeLock();

        this.objectLimit = 0L;
    }

    private final Map<UUID, AObject> objects;
    private final Map<Class<? extends AObject>, AObject> classedObjects;
    private final Lock objectReadLock;
    private final Lock objectWriteLock;
    private long objectLimit;

    public Map<UUID, AObject> getObjects() {
        return this.objects;
    }

    public Map<Class<? extends AObject>, AObject> getClassedObjects() {
        return this.classedObjects;
    }

    public Lock getObjectLock(long lock) {
        if (LogicalUtil.isAnyEqual(lock, LockType.READ)) {
            return this.objectReadLock;
        } else if (LogicalUtil.isAnyEqual(lock, LockType.WRITE)) {
            return this.objectWriteLock;
        } else {
            return null;
        }
    }

    public long getObjectLimit() {
        return this.objectLimit;
    }

    public void setObjectLimit(long coreObjectLimit) {
        this.objectLimit = coreObjectLimit;
    }
}
