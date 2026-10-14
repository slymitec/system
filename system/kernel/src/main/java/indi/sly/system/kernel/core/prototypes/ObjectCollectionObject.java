package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.environment.containers.ASystemSpace;
import indi.sly.system.kernel.core.environment.containers.UserSpace;
import indi.sly.system.kernel.core.environment.values.SpaceType;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ObjectCollectionObject extends AObject {
    private ASystemSpace getSpace(long space) {
        if (LogicalUtil.isAnyEqual(space, SpaceType.KERNEL)) {
            return this.coreManager.getKernelSpace();
        } else if (LogicalUtil.isAnyEqual(space, SpaceType.USER)) {
            UserSpace userSpace = this.coreManager.getUserSpace();

            if (ObjectUtil.isNull(userSpace)) {
                throw new StatusNotReadyException();
            }

            return userSpace;
        } else {
            throw new ConditionParametersException();
        }
    }

    public Lock getLock(long space, long lock) {
        return this.getSpace(space).getObjectLock(lock);
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

            AObject object = objects.get(id);
            if (ObjectUtil.isNull(object)) {
                throw new StatusNotExistedException();
            }

            return (T) object;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends AObject> T getByClass(long space, Class<T> clazz) {
        if (ObjectUtil.isNull(clazz)) {
            throw new ConditionParametersException();
        }

        Lock lock = this.getLock(space, LockType.READ);

        lock.lock();
        try {
            Map<Class<? extends AObject>, AObject> classedObjects = this.getSpace(space).getClassedObjects();

            AObject object = classedObjects.get(clazz);
            if (ObjectUtil.isNull(object)) {
                throw new StatusNotExistedException();
            }

            return (T) object;
        } finally {
            lock.unlock();
        }
    }

    public <T extends AObject> void addById(long space, UUID id, T object) {
        if (ValueUtil.isAnyNullOrEmpty(id) || ObjectUtil.isNull(object)) {
            throw new ConditionParametersException();
        }

        if (ObjectUtil.isNull(object.coreManager)) {
            object.coreManager = this.coreManager;
        }

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
        if (ObjectUtil.isNull(object)) {
            throw new ConditionParametersException();
        }
        if (ObjectUtil.isNull(object.coreManager)) {
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
        if (ObjectUtil.isNull(clazz)) {
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

            AObject object = objects.get(id);
            if (ObjectUtil.isNull(object)) {
                throw new StatusNotExistedException();
            }

            objects.remove(id);
        } finally {
            lock.unlock();
        }
    }

    public <T extends AObject> void deleteByClass(long space, Class<T> clazz) {
        if (ObjectUtil.isNull(clazz)) {
            throw new ConditionParametersException();
        }

        Lock lock = this.getLock(space, LockType.WRITE);

        lock.lock();
        try {
            Map<Class<? extends AObject>, AObject> classedObjects = this.getSpace(space).getClassedObjects();

            AObject object = classedObjects.get(clazz);
            if (ObjectUtil.isNull(object)) {
                throw new StatusNotExistedException();
            }

            classedObjects.remove(clazz);
        } finally {
            lock.unlock();
        }
    }
}
