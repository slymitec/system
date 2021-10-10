package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.*;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.enviroment.values.ASpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.values.HandleEntryDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashSet;
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

    public Set<AObject> getAll(long space) {
        Set<AObject> coreObjects;

        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            coreObjects = new HashSet<>(this.getSpace(space).getCoreObjects().values());
        } finally {
            lock.unlock();
        }

        return CollectionUtil.unmodifiable(coreObjects);
    }

    public Set<UUID> getAllHandle(long space) {
        Set<UUID> handles;

        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            handles = this.getSpace(space).getHandledHandles().keySet();
        } finally {
            lock.unlock();
        }

        return CollectionUtil.unmodifiable(handles);
    }

    public int getSize(long space) {
        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            return this.getSpace(space).getHandledHandles().size();
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
    private <T extends AObject> T getByID(long space, UUID id) {
        Map<UUID, AObject> coreObjects = this.getSpace(space).getCoreObjects();

        AObject coreObject = coreObjects.getOrDefault(id, null);
        if (ObjectUtil.isAnyNull(coreObject)) {
            throw new StatusNotExistedException();
        }

        return (T) coreObject;
    }

    public <T extends AObject> T getByHandle(long space, UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
        }

        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            Map<UUID, HandleEntryDefinition> handledHandles = this.getSpace(space).getHandledHandles();

            HandleEntryDefinition handleEntry = handledHandles.getOrDefault(handle, null);
            if (ObjectUtil.isAnyNull(handleEntry)) {
                throw new StatusNotExistedException();
            }

            return this.getByID(space, handleEntry.getID());
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

            Map<Class<? extends AObject>, HandleEntryDefinition> classedHandles = this.getSpace(space).getClassedHandles();

            HandleEntryDefinition handleEntry = classedHandles.getOrDefault(clazz, null);
            if (ObjectUtil.isAnyNull(handleEntry)) {
                throw new StatusNotExistedException();
            }

            return this.getByID(space, handleEntry.getID());
        } finally {
            lock.unlock();
        }
    }

    private <T extends AObject> void addByID(long space, T coreObject, UUID id) {
        Map<UUID, AObject> coreObjects = this.getSpace(space).getCoreObjects();

        if (coreObjects.containsKey(id)) {
            throw new StatusAlreadyExistedException();
        }

        coreObjects.put(id, coreObject);
    }

    public <T extends AObject> void addByHandle(long space, UUID handle, T coreObject) {
        if (ValueUtil.isAnyNullOrEmpty(handle) || ObjectUtil.isAnyNull(coreObject)) {
            throw new ConditionParametersException();
        }

        if (ObjectUtil.isAnyNull(coreObject.factoryManager)) {
            coreObject.factoryManager = this.factoryManager;
        }

        UUID id = UUIDUtil.createRandom();
        Class<? extends AObject> clazz = coreObject.getClass();

        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            Map<UUID, HandleEntryDefinition> handledHandles = this.getSpace(space).getHandledHandles();

            if (handledHandles.size() >= this.getSpace(space).getCoreObjectLimit()) {
                throw new StatusInsufficientResourcesException();
            }
            if (handledHandles.containsKey(handle)) {
                throw new StatusAlreadyExistedException();
            }

            HandleEntryDefinition handleEntry = new HandleEntryDefinition();
            handleEntry.setHandle(handle);
            handleEntry.setSpace(SpaceType.KERNEL);
            handleEntry.setType(clazz);
            handleEntry.setID(id);

            handledHandles.put(handle, handleEntry);
            this.addByID(space, coreObject, id);
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
        UUID id = UUIDUtil.createRandom();
        Class<? extends AObject> clazz = coreObject.getClass();

        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            Map<UUID, HandleEntryDefinition> handledHandles = this.getSpace(space).getHandledHandles();
            Map<Class<? extends AObject>, HandleEntryDefinition> classedHandles = this.getSpace(space).getClassedHandles();

            if (handledHandles.size() > this.getSpace(space).getCoreObjectLimit()) {
                throw new StatusInsufficientResourcesException();
            }
            if (classedHandles.containsKey(clazz)) {
                throw new StatusAlreadyExistedException();
            }

            HandleEntryDefinition handleEntry = new HandleEntryDefinition();
            handleEntry.setHandle(handle);
            handleEntry.setSpace(SpaceType.KERNEL);
            handleEntry.setType(clazz);
            handleEntry.setID(id);

            handledHandles.put(handle, handleEntry);
            classedHandles.put(clazz, handleEntry);

            this.addByID(space, coreObject, id);
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

            Map<UUID, HandleEntryDefinition> handledHandles = this.getSpace(space).getHandledHandles();

            return handledHandles.containsKey(handle);
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

            Map<Class<? extends AObject>, HandleEntryDefinition> classedHandles = this.getSpace(space).getClassedHandles();

            return classedHandles.containsKey(clazz);
        } finally {
            lock.unlock();
        }
    }

    private void deleteByID(long space, UUID id) {
        ASpaceDefinition<?> aSpace = this.getSpace(space);

        Map<UUID, AObject> coreObjects = aSpace.getCoreObjects();


        AObject coreObject = coreObjects.getOrDefault(id, null);
        if (ObjectUtil.isAnyNull(coreObject)) {
            throw new StatusNotExistedException();
        }

        coreObjects.remove(id);
    }

    private HandleEntryDefinition deleteHandleEntryByHandle(long space, UUID handle) {
        Map<UUID, HandleEntryDefinition> handledHandles = this.getSpace(space).getHandledHandles();

        HandleEntryDefinition handleEntry = handledHandles.getOrDefault(handle, null);
        if (ObjectUtil.isAnyNull(handleEntry)) {
            throw new StatusNotExistedException();
        }

        handledHandles.remove(handle);

        return handleEntry;
    }

    private <T extends AObject> HandleEntryDefinition deleteHandleEntryByClass(long space, Class<T> clazz) {
        Map<Class<? extends AObject>, HandleEntryDefinition> classedHandles = this.getSpace(space).getClassedHandles();

        HandleEntryDefinition handleEntry = classedHandles.getOrDefault(clazz, null);
        if (ObjectUtil.isAnyNull(handleEntry)) {
            throw new StatusNotExistedException();
        }

        classedHandles.remove(clazz);

        return handleEntry;
    }

    public void deleteByHandle(long space, UUID handle) {
        if (ValueUtil.isAnyNullOrEmpty(handle)) {
            throw new ConditionParametersException();
        }

        Lock lock = this.getLock(space, LockType.WRITE);

        try {
            lock.lock();

            HandleEntryDefinition handleEntry = this.deleteHandleEntryByHandle(space, handle);

            this.deleteByID(space, handleEntry.getID());
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

            HandleEntryDefinition handleEntry = this.deleteHandleEntryByClass(space, clazz);

            this.deleteHandleEntryByHandle(space, handleEntry.getHandle());
            this.deleteByID(space, handleEntry.getID());
        } finally {
            lock.unlock();
        }
    }
}
