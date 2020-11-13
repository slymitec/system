package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.exceptions.*;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.StringUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.enviroment.KernelSpace;
import indi.sly.system.kernel.core.enviroment.SpaceTypes;
import indi.sly.system.kernel.core.enviroment.UserSpace;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CoreObjectRepositoryObject extends ACoreObject {
    private KernelSpace kernelSpace;

    private KernelSpace getKernelSpace() {
        if (ObjectUtils.isAnyNull(this.kernelSpace)) {
            this.kernelSpace = this.factoryManager.getKernelSpace();
        }

        return this.kernelSpace;
    }

    private UserSpace getUserSpace() {
        return this.factoryManager.getUserSpace();
    }

    public Lock getLock(long spaceType, long lockType) {
        Lock lock = null;

        if (spaceType == SpaceTypes.KERNEL) {
            if (lockType == LockTypes.READ) {
                lock = this.getKernelSpace().getCoreObjectLock().readLock();
            } else if (lockType == LockTypes.WRITE) {
                lock = this.getKernelSpace().getCoreObjectLock().writeLock();
            }
        } else if (spaceType == SpaceTypes.USER) {
            if (lockType == LockTypes.READ) {
                lock = this.getUserSpace().getInfoObjectLock().readLock();
            } else if (lockType == LockTypes.WRITE) {
                lock = this.getUserSpace().getInfoObjectLock().writeLock();
            }
        }

        if (ObjectUtils.isAnyNull(lock)) {
            throw new StatusNotSupportedException();
        }

        return lock;
    }

    public Set<ACoreObject> getAll(long spaceType) {
        Map<UUID, ACoreObject> coreObjects;

        if (spaceType == SpaceTypes.KERNEL) {
            coreObjects = this.getKernelSpace().getCoreObjects();
        } else if (spaceType == SpaceTypes.USER) {
            coreObjects = new HashMap<>();

            for (Entry<UUID, InfoObject> pair : this.getUserSpace().getInfoObjects().entrySet()) {
                coreObjects.put(pair.getKey(), pair.getValue());
            }
        } else if (spaceType == SpaceTypes.ALL) {
            coreObjects = new HashMap<>(this.getKernelSpace().getCoreObjects());

            for (Entry<UUID, InfoObject> pair : this.getUserSpace().getInfoObjects().entrySet()) {
                coreObjects.put(pair.getKey(), pair.getValue());
            }
        } else {
            throw new ConditionParametersException();
        }

        return new HashSet<>(coreObjects.values());
    }

    public <T extends ACoreObject> T get(long spaceType, Class<T> clazz) {
        if (ObjectUtils.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.READ);

            try {
                lock.lock();

                UUID id = this.getIDByClass(spaceType, clazz);
                T coreObject = this.getByID(spaceType, clazz, id);

                return coreObject;
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACoreObject> T getByName(long spaceType, Class<T> clazz, String name) {
        if (ObjectUtils.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }
        if (StringUtils.isNameIllegal(name)) {
            name = clazz.getName();
        }

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.READ);

            try {
                lock.lock();

                UUID id = this.getIDByName(spaceType, name);
                T coreObject = this.getByID(spaceType, clazz, id);

                return coreObject;
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ACoreObject> T getByID(long spaceType, Class<T> clazz, UUID id) {
        if (ObjectUtils.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }
        if (UUIDUtils.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.READ);

            try {
                lock.lock();

                Map<UUID, ACoreObject> coreObjects = this.getKernelSpace().getCoreObjects();

                ACoreObject coreObject = coreObjects.getOrDefault(id, null);
                if (ObjectUtils.isAnyNull(coreObject)) {
                    throw new StatusNotExistedException();
                } else if (coreObject.getClass() != clazz) {
                    throw new StatusRelationshipErrorException();
                }

                return (T) coreObject;
            } finally {
                lock.unlock();
            }
        } else if (spaceType == SpaceTypes.USER) {
            Lock lock = this.getLock(spaceType, LockTypes.READ);

            try {
                lock.lock();

                Map<UUID, InfoObject> coreObjects = this.getUserSpace().getInfoObjects();

                InfoObject coreObject = coreObjects.getOrDefault(id, null);
                if (ObjectUtils.isAnyNull(coreObject)) {
                    throw new StatusNotExistedException();
                } else if (coreObject.getClass() != clazz) {
                    throw new StatusRelationshipErrorException();
                }

                return (T) coreObject;
            } finally {
                lock.unlock();
            }
        } else {
            throw new ConditionParametersException();
        }
    }

    public <T extends ACoreObject> UUID getIDByName(long spaceType, String name) {
        if (StringUtils.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.READ);

            try {
                lock.lock();

                Map<String, UUID> namedCoreObjectIDs = this.getKernelSpace().getNamedCoreObjectIDs();

                UUID id = namedCoreObjectIDs.getOrDefault(name, null);
                if (UUIDUtils.isAnyNullOrEmpty(id)) {
                    throw new StatusNotExistedException();
                }

                return id;
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACoreObject> UUID getIDByClass(long spaceType, Class<T> clazz) {
        if (ObjectUtils.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.READ);

            try {
                lock.lock();

                Map<Class<? extends ACoreObject>, UUID> classedCoreObjectIDs = this.getKernelSpace().getClassedCoreObjectIDs();

                UUID id = classedCoreObjectIDs.getOrDefault(clazz, null);
                if (UUIDUtils.isAnyNullOrEmpty(id)) {
                    throw new StatusNotExistedException();
                }

                return id;
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T> Set<ACoreObject> getByImplementInterface(long spaceType, Class<T> clazz) {
        if (!clazz.isInterface()) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.READ);

            try {
                lock.lock();

                Set<ACoreObject> allCoreObjects = this.getAll(spaceType);
                Set<ACoreObject> allCompoundCoreObjects = new HashSet<>();

                for (ACoreObject pair : allCoreObjects) {
                    Class<?>[] pairImplementInterfaces = pair.getClass().getInterfaces();

                    for (Class<?> pair2 : pairImplementInterfaces) {
                        if (pair2.equals(clazz)) {
                            allCompoundCoreObjects.add(pair);
                        }
                    }
                }

                return allCompoundCoreObjects;
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACoreObject> void add(long spaceType, T coreObject) {
        this.add(spaceType, null, null, coreObject);
    }

    public <T extends ACoreObject> void add(long spaceType, UUID id, String name, T coreObject) {
        if (UUIDUtils.isAnyNullOrEmpty(id) && !StringUtils.isNameIllegal(name)) {
            this.addByName(spaceType, name, coreObject);
        } else if (!UUIDUtils.isAnyNullOrEmpty(id) && StringUtils.isNameIllegal(name)) {
            this.addByID(spaceType, id, coreObject);
        }
        if (ObjectUtils.isAnyNull(coreObject)) {
            throw new ConditionParametersException();
        }

        coreObject.factoryManager = this.factoryManager;

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<UUID, ACoreObject> coreObjects = this.getKernelSpace().getCoreObjects();

                id = UUIDUtils.createRandom();
                Class<? extends ACoreObject> clazz = coreObject.getClass();

                Map<Class<? extends ACoreObject>, UUID> classedCoreObjectIDs = this.getKernelSpace().getClassedCoreObjectIDs();

                if (classedCoreObjectIDs.containsKey(clazz)) {
                    throw new StatusAlreadyExistedException();
                }
                classedCoreObjectIDs.put(clazz, id);

                coreObjects.put(id, coreObject);
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACoreObject> void addByName(long spaceType, String name, T coreObject) {
        if (ObjectUtils.isAnyNull(coreObject)) {
            throw new ConditionParametersException();
        }
        if (StringUtils.isNameIllegal(name)) {
            name = coreObject.getClass().getName();
        }

        coreObject.factoryManager = this.factoryManager;

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<UUID, ACoreObject> coreObjects = this.getKernelSpace().getCoreObjects();

                UUID id = UUIDUtils.createRandom();

                Map<String, UUID> namedCoreObjectIDs = this.getKernelSpace().getNamedCoreObjectIDs();

                if (namedCoreObjectIDs.containsKey(name)) {
                    throw new StatusAlreadyExistedException();
                }
                namedCoreObjectIDs.put(name, id);

                coreObjects.put(id, coreObject);
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACoreObject> void addByID(long spaceType, UUID id, T coreObject) {
        if (UUIDUtils.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }
        if (ObjectUtils.isAnyNull(coreObject)) {
            throw new ConditionParametersException();
        }

        coreObject.factoryManager = this.factoryManager;

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<UUID, ACoreObject> coreObjects = this.getKernelSpace().getCoreObjects();

                if (coreObjects.containsKey(id)) {
                    throw new StatusAlreadyExistedException();
                }

                coreObjects.put(id, coreObject);
            } finally {
                lock.unlock();
            }
        } else if (spaceType == SpaceTypes.USER) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<UUID, InfoObject> coreObjects = this.getUserSpace().getInfoObjects();

                if (coreObjects.containsKey(id)) {
                    throw new StatusAlreadyExistedException();
                }

                if (!(coreObject instanceof InfoObject)) {
                    throw new StatusRelationshipErrorException();
                }

                coreObjects.put(id, (InfoObject) coreObject);
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACoreObject> boolean contain(long spaceType, Class<T> clazz) {
        if (ObjectUtils.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<Class<? extends ACoreObject>, UUID> classedCoreObjectIDs = this.getKernelSpace().getClassedCoreObjectIDs();

                UUID id = classedCoreObjectIDs.getOrDefault(clazz, null);
                boolean isContain = this.containByID(spaceType, clazz, id);

                return isContain;
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACoreObject> boolean containByName(long spaceType, Class<T> clazz, String name) {
        if (ObjectUtils.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }
        if (StringUtils.isNameIllegal(name)) {
            name = clazz.getName();
        }

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<String, UUID> namedCoreObjectIDs = this.getKernelSpace().getNamedCoreObjectIDs();

                UUID id = namedCoreObjectIDs.getOrDefault(name, null);
                boolean isContain = this.containByID(spaceType, clazz, id);

                return isContain;
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACoreObject> boolean containByID(long spaceType, Class<T> clazz, UUID id) {
        if (ObjectUtils.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }
        if (UUIDUtils.isAnyNullOrEmpty(id)) {
            return false;
        }

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<UUID, ACoreObject> coreObjects = this.getKernelSpace().getCoreObjects();

                ACoreObject coreObject = coreObjects.getOrDefault(id, null);
                boolean isContain;
                if (ObjectUtils.isAnyNull(coreObject) || coreObject.getClass() != clazz) {
                    isContain = false;
                } else {
                    isContain = true;
                }

                return isContain;
            } finally {
                lock.unlock();
            }
        } else if (spaceType == SpaceTypes.USER) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<UUID, InfoObject> coreObjects = this.getUserSpace().getInfoObjects();

                InfoObject coreObject = coreObjects.getOrDefault(id, null);
                boolean isContain;
                if (ObjectUtils.isAnyNull(coreObject) || coreObject.getClass() != clazz) {
                    isContain = false;
                } else {
                    isContain = true;
                }


                return isContain;
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACoreObject> void delete(long spaceType, Class<T> clazz) {
        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<Class<? extends ACoreObject>, UUID> classedCoreObjectIDs = this.getKernelSpace().getClassedCoreObjectIDs();
                Map<UUID, ACoreObject> coreObjects = this.getKernelSpace().getCoreObjects();

                UUID id = this.getIDByClass(spaceType, clazz);
                this.getByID(spaceType, clazz, id);

                coreObjects.remove(id);
                classedCoreObjectIDs.remove(clazz);
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACoreObject> void deleteByName(long spaceType, Class<T> clazz, String name) {
        this.containByName(spaceType, clazz, name);

        if (StringUtils.isNameIllegal(name)) {
            name = clazz.getName();
        }

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<String, UUID> namedCoreObjectIDs = this.getKernelSpace().getNamedCoreObjectIDs();

                UUID id = namedCoreObjectIDs.getOrDefault(name, null);
                if (UUIDUtils.isAnyNullOrEmpty(id)) {
                    throw new StatusNotExistedException();
                }

                Map<UUID, ACoreObject> coreObjects = this.getKernelSpace().getCoreObjects();

                this.getByID(spaceType, clazz, id);

                coreObjects.remove(id);
                namedCoreObjectIDs.remove(name);
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACoreObject> void deleteByID(long spaceType, Class<T> clazz, UUID id) {
        if (ObjectUtils.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }
        if (UUIDUtils.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<String, UUID> namedCoreObjectIDs = this.getKernelSpace().getNamedCoreObjectIDs();
                Map<Class<? extends ACoreObject>, UUID> classedCoreObjectIDs = this.getKernelSpace().getClassedCoreObjectIDs();

                for (Entry<String, UUID> pair : namedCoreObjectIDs.entrySet()) {
                    if (pair.getValue() == id) {
                        namedCoreObjectIDs.remove(pair.getKey());
                        break;
                    }
                }
                for (Entry<Class<? extends ACoreObject>, UUID> pair : classedCoreObjectIDs.entrySet()) {
                    if (pair.getValue() == id) {
                        classedCoreObjectIDs.remove(pair.getKey());
                        break;
                    }
                }

                Map<UUID, ACoreObject> coreObjects = this.getKernelSpace().getCoreObjects();

                this.getByID(spaceType, clazz, id);

                coreObjects.remove(id);
            } finally {
                lock.unlock();
            }
        } else if (spaceType == SpaceTypes.USER) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<UUID, InfoObject> coreObjects = this.getUserSpace().getInfoObjects();

                this.getByID(spaceType, clazz, id);

                coreObjects.remove(id);
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }
}

