package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.*;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.enviroment.values.ASpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ObjectCollectionObject extends AObject {
    private ASpaceDefinition getSpace(long space) {
        if (LogicalUtil.isAnyEqual(space, SpaceType.KERNEL)) {
            return this.coreManager.getKernelSpace();
        } else if (LogicalUtil.isAnyEqual(space, SpaceType.USER)) {
            return this.coreManager.getUserSpace();
        } else {
            throw new ConditionParametersException();
        }
    }

    public Lock getLock(long space, long lock) {
        Lock readWriteLock = this.getSpace(space).getObjectLock(lock);

        if (ObjectUtil.isAnyNull(readWriteLock)) {
            throw new StatusNotSupportedException();
        }

        return readWriteLock;
    }

    public long getLimit(long space) {
        Lock lock = this.getLock(space, LockType.READ);

        lock.lock();
        try {
            return this.getSpace(space).getObjectLimit();
        } finally {
            lock.unlock();
        }
    }

    public void setLimit(long space, long limit) {
        Lock lock = this.getLock(space, LockType.WRITE);

        lock.lock();
        try {
            this.getSpace(space).setObjectLimit(limit);
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends AObject> T getById(long space, UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        Lock lock = this.getLock(space, LockType.READ);

        lock.lock();
        try {
            Map<UUID, AObject> objects = this.getSpace(space).getObjects();

            AObject object = objects.getOrDefault(id, null);
            if (ObjectUtil.isAnyNull(object)) {
                throw new StatusNotExistedException();
            }

            return (T) object;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends AObject> T getByClass(long space, Class<T> clazz) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        Lock lock = this.getLock(space, LockType.READ);

        lock.lock();
        try {
            Map<Class<? extends AObject>, AObject> classedObjects = this.getSpace(space).getClassedObjects();

            AObject object = classedObjects.getOrDefault(clazz, null);
            if (ObjectUtil.isAnyNull(object)) {
                throw new StatusNotExistedException();
            }

            return (T) object;
        } finally {
            lock.unlock();
        }
    }

    public <T extends AObject> void addById(long space, UUID id, T object) {
        if (ValueUtil.isAnyNullOrEmpty(id) || ObjectUtil.isAnyNull(object)) {
            throw new ConditionParametersException();
        }

        if (ObjectUtil.isAnyNull(object.coreManager)) {
            object.coreManager = this.coreManager;
        }

        Class<? extends AObject> clazz = object.getClass();

        Lock lock = this.getLock(space, LockType.READ);

        lock.lock();
        try {
            Map<UUID, AObject> objects = this.getSpace(space).getObjects();

            if (objects.containsKey(id)) {
                throw new StatusAlreadyExistedException();
            }

            objects.put(id, object);
        } finally {
            lock.unlock();
        }
    }

    public <T extends AObject> void addByClass(long space, T object) {
        if (ObjectUtil.isAnyNull(object)) {
            throw new ConditionParametersException();
        }
        if (ObjectUtil.isAnyNull(object.coreManager)) {
            object.coreManager = this.coreManager;
        }

        Class<? extends AObject> clazz = object.getClass();

        Lock lock = this.getLock(space, LockType.READ);

        lock.lock();
        try {
            Map<Class<? extends AObject>, AObject> classedObjects = this.getSpace(space).getClassedObjects();

            if (classedObjects.containsKey(clazz)) {
                throw new StatusAlreadyExistedException();
            }

            classedObjects.put(clazz, object);
        } finally {
            lock.unlock();
        }
    }

    public boolean containByID(long space, UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        Lock lock = this.getLock(space, LockType.READ);

        lock.lock();
        try {
            Map<UUID, AObject> objects = this.getSpace(space).getObjects();

            return objects.containsKey(id);
        } finally {
            lock.unlock();
        }
    }

    public <T extends AObject> boolean containByClass(long space, Class<T> clazz) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        Lock lock = this.getLock(space, LockType.READ);

        lock.lock();
        try {
            Map<Class<? extends AObject>, AObject> classedObjects = this.getSpace(space).getClassedObjects();

            return classedObjects.containsKey(clazz);
        } finally {
            lock.unlock();
        }
    }

    public void deleteById(long space, UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        Lock lock = this.getLock(space, LockType.WRITE);

        lock.lock();
        try {
            Map<UUID, AObject> objects = this.getSpace(space).getObjects();

            AObject object = objects.getOrDefault(id, null);
            if (ObjectUtil.isAnyNull(object)) {
                throw new StatusNotExistedException();
            }

            objects.remove(id);
        } finally {
            lock.unlock();
        }
    }

    public <T extends AObject> void deleteByClass(long space, Class<T> clazz) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        Lock lock = this.getLock(space, LockType.WRITE);

        lock.lock();
        try {
            Map<Class<? extends AObject>, AObject> classedObjects = this.getSpace(space).getClassedObjects();

            AObject object = classedObjects.getOrDefault(clazz, null);
            if (ObjectUtil.isAnyNull(object)) {
                throw new StatusNotExistedException();
            }

            classedObjects.remove(clazz);
        } finally {
            lock.unlock();
        }
    }
}
