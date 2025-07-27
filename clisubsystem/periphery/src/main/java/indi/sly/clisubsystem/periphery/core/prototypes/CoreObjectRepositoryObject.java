package indi.sly.clisubsystem.periphery.core.prototypes;

import indi.sly.clisubsystem.periphery.core.enviroment.values.ASpaceDefinition;
import indi.sly.clisubsystem.periphery.core.enviroment.values.SpaceType;
import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.*;
import indi.sly.system.common.values.LockType;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CoreObjectRepositoryObject extends AObject {
    private ASpaceDefinition<?> getSpace(long space) {
        if (LogicalUtil.isAnyEqual(space, SpaceType.KERNEL)) {
            return this.factoryManager.getKernelSpace();
        } else if (LogicalUtil.isAnyEqual(space, SpaceType.USER)) {
            return this.factoryManager.getUserSpace();
        } else {
            throw new ConditionParametersException();
        }
    }

    public Lock getLock(long space, long lock) {
        Lock readWriteLock = this.getSpace(space).getCoreObjectLock(lock);

        if (ObjectUtil.isAnyNull(readWriteLock)) {
            throw new StatusNotSupportedException();
        }

        return readWriteLock;
    }

    public Set<UUID> getAllHandles(long space) {
        Set<UUID> handles;

        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            handles = this.getSpace(space).getHandledObjects().keySet();
        } finally {
            lock.unlock();
        }

        return CollectionUtil.unmodifiable(handles);
    }

    public int getSize(long space) {
        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            return this.getSpace(space).getHandledObjects().size();
        } finally {
            lock.unlock();
        }
    }

    public long getLimit(long space) {
        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            return this.getSpace(space).getCoreObjectLimit();
        } finally {
            lock.unlock();
        }
    }

    public void setLimit(long space, long limit) {
        Lock lock = this.getLock(space, LockType.WRITE);

        try {
            lock.lock();

            this.getSpace(space).setCoreObjectLimit(limit);
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends AObject> T getByHandle(long space, UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
        }

        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            Map<UUID, AObject> coreObjects = this.getSpace(space).getHandledObjects();

            AObject coreObject = coreObjects.getOrDefault(handle, null);
            if (ObjectUtil.isAnyNull(coreObject)) {
                throw new StatusNotExistedException();
            }

            return (T) coreObject;
        } finally {
            lock.unlock();
        }
    }

    public <T extends AObject> T getByClass(long space, Class<T> clazz) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        ASpaceDefinition<?> aSpace = this.getSpace(space);
        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            Map<Class<? extends AObject>, UUID> classedHandles = this.getSpace(space).getClassedHandles();

            UUID handle = classedHandles.getOrDefault(clazz, null);
            if (ValueUtil.isAnyNullOrEmpty(handle)) {
                throw new StatusNotExistedException();
            }

            return this.getByHandle(space, handle);
        } finally {
            lock.unlock();
        }
    }

    public <T extends AObject> void addByHandle(long space, UUID handle, T coreObject) {
        if (ValueUtil.isAnyNullOrEmpty(handle) || ObjectUtil.isAnyNull(coreObject)) {
            throw new ConditionParametersException();
        }

        if (ObjectUtil.isAnyNull(coreObject.factoryManager)) {
            coreObject.factoryManager = this.factoryManager;
        }

        Class<? extends AObject> clazz = coreObject.getClass();

        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            Map<UUID, AObject> handledObjects = this.getSpace(space).getHandledObjects();

            if (handledObjects.size() >= this.getSpace(space).getCoreObjectLimit()) {
                throw new StatusInsufficientResourcesException();
            }
            if (handledObjects.containsKey(handle)) {
                throw new StatusAlreadyExistedException();
            }

            handledObjects.put(handle, coreObject);
        } finally {
            lock.unlock();
        }
    }

    public <T extends AObject> void addByClass(long space, T coreObject) {
        if (ObjectUtil.isAnyNull(coreObject)) {
            throw new ConditionParametersException();
        }
        if (ObjectUtil.isAnyNull(coreObject.factoryManager)) {
            coreObject.factoryManager = this.factoryManager;
        }

        UUID handle = UUIDUtil.createRandom();
        Class<? extends AObject> clazz = coreObject.getClass();

        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            Map<UUID, AObject> handledObjects = this.getSpace(space).getHandledObjects();
            Map<Class<? extends AObject>, UUID> classedHandles = this.getSpace(space).getClassedHandles();

            if (handledObjects.size() > this.getSpace(space).getCoreObjectLimit()) {
                throw new StatusInsufficientResourcesException();
            }
            if (classedHandles.containsKey(clazz)) {
                throw new StatusAlreadyExistedException();
            }

            handledObjects.put(handle, coreObject);
            classedHandles.put(clazz, handle);
        } finally {
            lock.unlock();
        }
    }

    public boolean containByHandle(long space, UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
        }

        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            Map<UUID, AObject> handledObjects = this.getSpace(space).getHandledObjects();

            return handledObjects.containsKey(handle);
        } finally {
            lock.unlock();
        }
    }

    public <T extends AObject> boolean containByClass(long space, Class<T> clazz) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            Map<Class<? extends AObject>, UUID> classedHandles = this.getSpace(space).getClassedHandles();

            return classedHandles.containsKey(clazz);
        } finally {
            lock.unlock();
        }
    }

    public void deleteByHandle(long space, UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
        }

        Lock lock = this.getLock(space, LockType.WRITE);

        try {
            lock.lock();

            ASpaceDefinition<?> aSpace = this.getSpace(space);

            Map<UUID, AObject> coreObjects = aSpace.getHandledObjects();


            AObject coreObject = coreObjects.getOrDefault(handle, null);
            if (ObjectUtil.isAnyNull(coreObject)) {
                throw new StatusNotExistedException();
            }

            coreObjects.remove(handle);
        } finally {
            lock.unlock();
        }
    }

    public <T extends AObject> void deleteByClass(long space, Class<T> clazz) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        Lock lock = this.getLock(space, LockType.WRITE);

        try {
            lock.lock();

            Map<Class<? extends AObject>, UUID> classedHandles = this.getSpace(space).getClassedHandles();

            UUID handle = classedHandles.getOrDefault(clazz, null);
            if (ValueUtil.isAnyNullOrEmpty(handle)) {
                throw new StatusNotExistedException();
            }

            classedHandles.remove(clazz);

            this.deleteByHandle(space, handle);
        } finally {
            lock.unlock();
        }
    }
}

