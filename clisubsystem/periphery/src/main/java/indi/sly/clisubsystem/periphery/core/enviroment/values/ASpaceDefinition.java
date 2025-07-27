package indi.sly.clisubsystem.periphery.core.enviroment.values;

import indi.sly.clisubsystem.periphery.core.prototypes.AObject;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.values.ADefinition;
import indi.sly.system.common.values.LockType;
import jakarta.inject.Named;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Named
public abstract class ASpaceDefinition<T> extends ADefinition<T> {
    public ASpaceDefinition() {
        this.handledObjects = new ConcurrentHashMap<>();
        this.classedHandles = new ConcurrentHashMap<>();
        ReentrantReadWriteLock coreObjectLock = new ReentrantReadWriteLock();
        this.coreObjectReadLock = coreObjectLock.readLock();
        this.coreObjectWriteLock = coreObjectLock.writeLock();
        this.coreObjectLimit = 0L;
    }

    private final Map<UUID, AObject> handledObjects;
    private final Map<Class<? extends AObject>, UUID> classedHandles;
    private final Lock coreObjectReadLock;
    private final Lock coreObjectWriteLock;
    private long coreObjectLimit;

    public Map<UUID, AObject> getHandledObjects() {
        return this.handledObjects;
    }

    public Map<Class<? extends AObject>, UUID> getClassedHandles() {
        return this.classedHandles;
    }

    public Lock getCoreObjectLock(long lock) {
        if (LogicalUtil.isAnyEqual(lock, LockType.READ)) {
            return this.coreObjectReadLock;
        } else if (LogicalUtil.isAnyEqual(lock, LockType.WRITE)) {
            return this.coreObjectWriteLock;
        } else {
            return null;
        }
    }

    public long getCoreObjectLimit() {
        return this.coreObjectLimit;
    }

    public void setCoreObjectLimit(long coreObjectLimit) {
        this.coreObjectLimit = coreObjectLimit;
    }
}
