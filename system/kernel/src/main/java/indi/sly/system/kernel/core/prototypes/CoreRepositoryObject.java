package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.exceptions.*;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.StringUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.enviroment.KernelSpace;
import indi.sly.system.kernel.core.enviroment.types.SpaceTypes;
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
public class CoreRepositoryObject extends ACorePrototype {
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
                lock = this.getKernelSpace().getCorePrototypeLock().readLock();
            } else if (lockType == LockTypes.WRITE) {
                lock = this.getKernelSpace().getCorePrototypeLock().writeLock();
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

    public Set<ACorePrototype> getAll(long spaceType) {
        Map<UUID, ACorePrototype> corePrototypes;

        if (spaceType == SpaceTypes.KERNEL) {
            corePrototypes = this.getKernelSpace().getCorePrototypes();
        } else if (spaceType == SpaceTypes.USER) {
            corePrototypes = new HashMap<>();

            for (Entry<UUID, InfoObject> pair : this.getUserSpace().getInfoObjects().entrySet()) {
                corePrototypes.put(pair.getKey(), pair.getValue());
            }
        } else if (spaceType == SpaceTypes.ALL) {
            corePrototypes = new HashMap<>(this.getKernelSpace().getCorePrototypes());

            for (Entry<UUID, InfoObject> pair : this.getUserSpace().getInfoObjects().entrySet()) {
                corePrototypes.put(pair.getKey(), pair.getValue());
            }
        } else {
            throw new ConditionParametersException();
        }

        return Collections.unmodifiableSet(new HashSet<>(corePrototypes.values()));
    }

    public <T extends ACorePrototype> T get(long spaceType, Class<T> clazz) {
        if (ObjectUtils.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.READ);

            try {
                lock.lock();

                UUID id = this.getIDByClass(spaceType, clazz);
                T corePrototype = this.getByID(spaceType, clazz, id);

                return corePrototype;
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACorePrototype> T getByName(long spaceType, Class<T> clazz, String name) {
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
                T corePrototype = this.getByID(spaceType, clazz, id);

                return corePrototype;
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ACorePrototype> T getByID(long spaceType, Class<T> clazz, UUID id) {
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

                Map<UUID, ACorePrototype> corePrototypes = this.getKernelSpace().getCorePrototypes();

                ACorePrototype corePrototype = corePrototypes.getOrDefault(id, null);
                if (ObjectUtils.isAnyNull(corePrototype)) {
                    throw new StatusNotExistedException();
                } else if (corePrototype.getClass() != clazz) {
                    throw new StatusRelationshipErrorException();
                }

                return (T) corePrototype;
            } finally {
                lock.unlock();
            }
        } else if (spaceType == SpaceTypes.USER) {
            Lock lock = this.getLock(spaceType, LockTypes.READ);

            try {
                lock.lock();

                Map<UUID, InfoObject> corePrototypes = this.getUserSpace().getInfoObjects();

                InfoObject corePrototype = corePrototypes.getOrDefault(id, null);
                if (ObjectUtils.isAnyNull(corePrototype)) {
                    throw new StatusNotExistedException();
                } else if (corePrototype.getClass() != clazz) {
                    throw new StatusRelationshipErrorException();
                }

                return (T) corePrototype;
            } finally {
                lock.unlock();
            }
        } else {
            throw new ConditionParametersException();
        }
    }

    public <T extends ACorePrototype> UUID getIDByName(long spaceType, String name) {
        if (StringUtils.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.READ);

            try {
                lock.lock();

                Map<String, UUID> namedCorePrototypeIDs = this.getKernelSpace().getNamedCorePrototypeIDs();

                UUID id = namedCorePrototypeIDs.getOrDefault(name, null);
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

    public <T extends ACorePrototype> UUID getIDByClass(long spaceType, Class<T> clazz) {
        if (ObjectUtils.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.READ);

            try {
                lock.lock();

                Map<Class<? extends ACorePrototype>, UUID> classedCorePrototypeIDs =
                        this.getKernelSpace().getClassedCorePrototypeIDs();

                UUID id = classedCorePrototypeIDs.getOrDefault(clazz, null);
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

    public <T> Set<ACorePrototype> getByImplementInterface(long spaceType, Class<T> clazz) {
        if (!clazz.isInterface()) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.READ);

            try {
                lock.lock();

                Set<ACorePrototype> corePrototypes = this.getAll(spaceType);
                Set<ACorePrototype> resultCorePrototypes = new HashSet<>();

                for (ACorePrototype pair : corePrototypes) {
                    Class<?>[] pairImplementInterfaces = pair.getClass().getInterfaces();

                    for (Class<?> pair2 : pairImplementInterfaces) {
                        if (pair2.equals(clazz)) {
                            resultCorePrototypes.add(pair);
                        }
                    }
                }

                return resultCorePrototypes;
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACorePrototype> void add(long spaceType, T corePrototype) {
        this.add(spaceType, null, null, corePrototype);
    }

    public <T extends ACorePrototype> void add(long spaceType, UUID id, String name, T corePrototype) {
        if (UUIDUtils.isAnyNullOrEmpty(id) && !StringUtils.isNameIllegal(name)) {
            this.addByName(spaceType, name, corePrototype);
        } else if (!UUIDUtils.isAnyNullOrEmpty(id) && StringUtils.isNameIllegal(name)) {
            this.addByID(spaceType, id, corePrototype);
        }
        if (ObjectUtils.isAnyNull(corePrototype)) {
            throw new ConditionParametersException();
        }

        corePrototype.factoryManager = this.factoryManager;

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<UUID, ACorePrototype> corePrototypes = this.getKernelSpace().getCorePrototypes();

                id = UUIDUtils.createRandom();
                Class<? extends ACorePrototype> clazz = corePrototype.getClass();

                Map<Class<? extends ACorePrototype>, UUID> classedCorePrototypeIDs =
                        this.getKernelSpace().getClassedCorePrototypeIDs();

                if (classedCorePrototypeIDs.containsKey(clazz)) {
                    throw new StatusAlreadyExistedException();
                }
                classedCorePrototypeIDs.put(clazz, id);

                corePrototypes.put(id, corePrototype);
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACorePrototype> void addByName(long spaceType, String name, T corePrototype) {
        if (ObjectUtils.isAnyNull(corePrototype)) {
            throw new ConditionParametersException();
        }
        if (StringUtils.isNameIllegal(name)) {
            name = corePrototype.getClass().getName();
        }

        corePrototype.factoryManager = this.factoryManager;

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<UUID, ACorePrototype> corePrototypes = this.getKernelSpace().getCorePrototypes();

                UUID id = UUIDUtils.createRandom();

                Map<String, UUID> namedCorePrototypeIDs = this.getKernelSpace().getNamedCorePrototypeIDs();

                if (namedCorePrototypeIDs.containsKey(name)) {
                    throw new StatusAlreadyExistedException();
                }
                namedCorePrototypeIDs.put(name, id);

                corePrototypes.put(id, corePrototype);
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACorePrototype> void addByID(long spaceType, UUID id, T corePrototype) {
        if (UUIDUtils.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }
        if (ObjectUtils.isAnyNull(corePrototype)) {
            throw new ConditionParametersException();
        }

        corePrototype.factoryManager = this.factoryManager;

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<UUID, ACorePrototype> corePrototypes = this.getKernelSpace().getCorePrototypes();

                if (corePrototypes.containsKey(id)) {
                    throw new StatusAlreadyExistedException();
                }

                corePrototypes.put(id, corePrototype);
            } finally {
                lock.unlock();
            }
        } else if (spaceType == SpaceTypes.USER) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<UUID, InfoObject> corePrototypes = this.getUserSpace().getInfoObjects();

                if (corePrototypes.containsKey(id)) {
                    throw new StatusAlreadyExistedException();
                }

                if (!(corePrototype instanceof InfoObject)) {
                    throw new StatusRelationshipErrorException();
                }

                corePrototypes.put(id, (InfoObject) corePrototype);
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACorePrototype> boolean contain(long spaceType, Class<T> clazz) {
        if (ObjectUtils.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<Class<? extends ACorePrototype>, UUID> classedCorePrototypeIDs =
                        this.getKernelSpace().getClassedCorePrototypeIDs();

                UUID id = classedCorePrototypeIDs.getOrDefault(clazz, null);
                boolean isContain = this.containByID(spaceType, clazz, id);

                return isContain;
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACorePrototype> boolean containByName(long spaceType, Class<T> clazz, String name) {
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

                Map<String, UUID> namedCorePrototypeIDs = this.getKernelSpace().getNamedCorePrototypeIDs();

                UUID id = namedCorePrototypeIDs.getOrDefault(name, null);
                boolean isContain = this.containByID(spaceType, clazz, id);

                return isContain;
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACorePrototype> boolean containByID(long spaceType, Class<T> clazz, UUID id) {
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

                Map<UUID, ACorePrototype> corePrototypes = this.getKernelSpace().getCorePrototypes();

                ACorePrototype corePrototype = corePrototypes.getOrDefault(id, null);
                boolean isContain;
                if (ObjectUtils.isAnyNull(corePrototype) || corePrototype.getClass() != clazz) {
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

                Map<UUID, InfoObject> corePrototypes = this.getUserSpace().getInfoObjects();

                InfoObject corePrototype = corePrototypes.getOrDefault(id, null);
                boolean isContain;
                if (ObjectUtils.isAnyNull(corePrototype) || corePrototype.getClass() != clazz) {
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

    public <T extends ACorePrototype> void delete(long spaceType, Class<T> clazz) {
        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<Class<? extends ACorePrototype>, UUID> classedCorePrototypeIDs =
                        this.getKernelSpace().getClassedCorePrototypeIDs();
                Map<UUID, ACorePrototype> corePrototypes = this.getKernelSpace().getCorePrototypes();

                UUID id = this.getIDByClass(spaceType, clazz);
                this.getByID(spaceType, clazz, id);

                corePrototypes.remove(id);
                classedCorePrototypeIDs.remove(clazz);
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACorePrototype> void deleteByName(long spaceType, Class<T> clazz, String name) {
        this.containByName(spaceType, clazz, name);

        if (StringUtils.isNameIllegal(name)) {
            name = clazz.getName();
        }

        if (spaceType == SpaceTypes.KERNEL) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<String, UUID> namedCorePrototypeIDs = this.getKernelSpace().getNamedCorePrototypeIDs();

                UUID id = namedCorePrototypeIDs.getOrDefault(name, null);
                if (UUIDUtils.isAnyNullOrEmpty(id)) {
                    throw new StatusNotExistedException();
                }

                Map<UUID, ACorePrototype> corePrototypes = this.getKernelSpace().getCorePrototypes();

                this.getByID(spaceType, clazz, id);

                corePrototypes.remove(id);
                namedCorePrototypeIDs.remove(name);
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends ACorePrototype> void deleteByID(long spaceType, Class<T> clazz, UUID id) {
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

                Map<String, UUID> namedCorePrototypeIDs = this.getKernelSpace().getNamedCorePrototypeIDs();
                Map<Class<? extends ACorePrototype>, UUID> classedCorePrototypeIDs =
                        this.getKernelSpace().getClassedCorePrototypeIDs();

                for (Entry<String, UUID> pair : namedCorePrototypeIDs.entrySet()) {
                    if (pair.getValue() == id) {
                        namedCorePrototypeIDs.remove(pair.getKey());
                        break;
                    }
                }
                for (Entry<Class<? extends ACorePrototype>, UUID> pair : classedCorePrototypeIDs.entrySet()) {
                    if (pair.getValue() == id) {
                        classedCorePrototypeIDs.remove(pair.getKey());
                        break;
                    }
                }

                Map<UUID, ACorePrototype> corePrototypes = this.getKernelSpace().getCorePrototypes();

                this.getByID(spaceType, clazz, id);

                corePrototypes.remove(id);
            } finally {
                lock.unlock();
            }
        } else if (spaceType == SpaceTypes.USER) {
            Lock lock = this.getLock(spaceType, LockTypes.WRITE);

            try {
                lock.lock();

                Map<UUID, InfoObject> corePrototypes = this.getUserSpace().getInfoObjects();

                this.getByID(spaceType, clazz, id);

                corePrototypes.remove(id);
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }
}
