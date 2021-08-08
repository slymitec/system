package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CorePrototypeRepositoryObject extends APrototype {
    private KernelSpaceDefinition kernelSpace;

    private KernelSpaceDefinition getKernelSpace() {
        if (ObjectUtil.isAnyNull(this.kernelSpace)) {
            this.kernelSpace = this.factoryManager.getKernelSpace();
        }

        return this.kernelSpace;
    }

    private UserSpaceDefinition getUserSpace() {
        return this.factoryManager.getUserSpace();
    }

    public Lock getLock(long space, long lock) {
        Lock readWriteLock = null;

        if (space == SpaceType.KERNEL) {
            if (lock == LockType.READ) {
                readWriteLock = this.getKernelSpace().getCorePrototypeLock().readLock();
            } else if (lock == LockType.WRITE) {
                readWriteLock = this.getKernelSpace().getCorePrototypeLock().writeLock();
            }
        } else if (space == SpaceType.USER) {
            if (lock == LockType.READ) {
                readWriteLock = this.getUserSpace().getInfoLock().readLock();
            } else if (lock == LockType.WRITE) {
                readWriteLock = this.getUserSpace().getInfoLock().writeLock();
            }
        }

        if (ObjectUtil.isAnyNull(readWriteLock)) {
            throw new StatusNotSupportedException();
        }

        return readWriteLock;
    }

    public Set<APrototype> getAll(long space) {
        Set<APrototype> corePrototypes = new HashSet<>();

        if (space == SpaceType.KERNEL) {
            corePrototypes.addAll(this.getKernelSpace().getCorePrototypes().values());
        } else {
            throw new ConditionParametersException();
        }

        return corePrototypes;
    }

    public <T extends APrototype> T get(long spaceType, Class<T> clazz) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceType.KERNEL) {
            Lock lock = this.getLock(spaceType, LockType.READ);

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

    public <T extends APrototype> T getByName(long spaceType, Class<T> clazz, String name) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }
        if (StringUtil.isNameIllegal(name)) {
            name = clazz.getName();
        }

        if (spaceType == SpaceType.KERNEL) {
            Lock lock = this.getLock(spaceType, LockType.READ);

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
    public <T extends APrototype> T getByID(long spaceType, Class<T> clazz, UUID id) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceType.KERNEL) {
            Lock lock = this.getLock(spaceType, LockType.READ);

            try {
                lock.lock();

                Map<UUID, APrototype> corePrototypes = this.getKernelSpace().getCorePrototypes();

                APrototype corePrototype = corePrototypes.getOrDefault(id, null);
                if (ObjectUtil.isAnyNull(corePrototype)) {
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

    public <T extends APrototype> UUID getIDByName(long spaceType, String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceType.KERNEL) {
            Lock lock = this.getLock(spaceType, LockType.READ);

            try {
                lock.lock();

                Map<String, UUID> namedCorePrototypeIDs = this.getKernelSpace().getNamedCorePrototypeIDs();

                UUID id = namedCorePrototypeIDs.getOrDefault(name, null);
                if (ValueUtil.isAnyNullOrEmpty(id)) {
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

    public <T extends APrototype> UUID getIDByClass(long spaceType, Class<T> clazz) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceType.KERNEL) {
            Lock lock = this.getLock(spaceType, LockType.READ);

            try {
                lock.lock();

                Map<Class<? extends APrototype>, UUID> classedCorePrototypeIDs =
                        this.getKernelSpace().getClassedCorePrototypeIDs();

                UUID id = classedCorePrototypeIDs.getOrDefault(clazz, null);
                if (ValueUtil.isAnyNullOrEmpty(id)) {
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

    public <T> Set<APrototype> getByImplementInterface(long space, Class<T> clazz) {
        if (!clazz.isInterface()) {
            throw new ConditionParametersException();
        }

        if (space == SpaceType.KERNEL) {
            Lock lock = this.getLock(space, LockType.READ);

            try {
                lock.lock();

                Set<APrototype> corePrototypes = this.getAll(space);
                Set<APrototype> resultCorePrototypes = new HashSet<>();

                for (APrototype corePrototype : corePrototypes) {
                    Class<?>[] corePrototypeInterfaces = corePrototype.getClass().getInterfaces();

                    for (Class<?> corePrototypeInterface : corePrototypeInterfaces) {
                        if (corePrototypeInterface.equals(clazz)) {
                            resultCorePrototypes.add(corePrototype);
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

    public <T extends APrototype> void add(long spaceType, T corePrototype) {
        this.add(spaceType, null, null, corePrototype);
    }

    public <T extends APrototype> void add(long spaceType, UUID id, String name, T corePrototype) {
        if (ValueUtil.isAnyNullOrEmpty(id) && !StringUtil.isNameIllegal(name)) {
            this.addByName(spaceType, name, corePrototype);
        } else if (!ValueUtil.isAnyNullOrEmpty(id) && StringUtil.isNameIllegal(name)) {
            this.addByID(spaceType, id, corePrototype);
        }
        if (ObjectUtil.isAnyNull(corePrototype)) {
            throw new ConditionParametersException();
        }

        corePrototype.factoryManager = this.factoryManager;

        if (spaceType == SpaceType.KERNEL) {
            Lock lock = this.getLock(spaceType, LockType.WRITE);

            try {
                lock.lock();

                Map<UUID, APrototype> corePrototypes = this.getKernelSpace().getCorePrototypes();

                id = UUIDUtil.createRandom();
                Class<? extends APrototype> clazz = corePrototype.getClass();

                Map<Class<? extends APrototype>, UUID> classedCorePrototypeIDs =
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

    public <T extends APrototype> void addByName(long spaceType, String name, T corePrototype) {
        if (ObjectUtil.isAnyNull(corePrototype)) {
            throw new ConditionParametersException();
        }
        if (StringUtil.isNameIllegal(name)) {
            name = corePrototype.getClass().getName();
        }

        corePrototype.factoryManager = this.factoryManager;

        if (spaceType == SpaceType.KERNEL) {
            Lock lock = this.getLock(spaceType, LockType.WRITE);

            try {
                lock.lock();

                Map<UUID, APrototype> corePrototypes = this.getKernelSpace().getCorePrototypes();

                UUID id = UUIDUtil.createRandom();

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

    public <T extends APrototype> void addByID(long spaceType, UUID id, T corePrototype) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }
        if (ObjectUtil.isAnyNull(corePrototype)) {
            throw new ConditionParametersException();
        }

        corePrototype.factoryManager = this.factoryManager;

        if (spaceType == SpaceType.KERNEL) {
            Lock lock = this.getLock(spaceType, LockType.WRITE);

            try {
                lock.lock();

                Map<UUID, APrototype> corePrototypes = this.getKernelSpace().getCorePrototypes();

                if (corePrototypes.containsKey(id)) {
                    throw new StatusAlreadyExistedException();
                }

                corePrototypes.put(id, corePrototype);
            } finally {
                lock.unlock();
            }
        } else {
            throw new StatusNotSupportedException();
        }
    }

    public <T extends APrototype> boolean contain(long spaceType, Class<T> clazz) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceType.KERNEL) {
            Lock lock = this.getLock(spaceType, LockType.WRITE);

            try {
                lock.lock();

                Map<Class<? extends APrototype>, UUID> classedCorePrototypeIDs =
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

    public <T extends APrototype> boolean containByName(long spaceType, Class<T> clazz, String name) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }
        if (StringUtil.isNameIllegal(name)) {
            name = clazz.getName();
        }

        if (spaceType == SpaceType.KERNEL) {
            Lock lock = this.getLock(spaceType, LockType.WRITE);

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

    public <T extends APrototype> boolean containByID(long spaceType, Class<T> clazz, UUID id) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            return false;
        }

        if (spaceType == SpaceType.KERNEL) {
            Lock lock = this.getLock(spaceType, LockType.WRITE);

            try {
                lock.lock();

                Map<UUID, APrototype> corePrototypes = this.getKernelSpace().getCorePrototypes();

                APrototype corePrototype = corePrototypes.getOrDefault(id, null);
                boolean isContain;
                if (ObjectUtil.isAnyNull(corePrototype) || corePrototype.getClass() != clazz) {
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

    public <T extends APrototype> void delete(long spaceType, Class<T> clazz) {
        if (spaceType == SpaceType.KERNEL) {
            Lock lock = this.getLock(spaceType, LockType.WRITE);

            try {
                lock.lock();

                Map<Class<? extends APrototype>, UUID> classedCorePrototypeIDs =
                        this.getKernelSpace().getClassedCorePrototypeIDs();
                Map<UUID, APrototype> corePrototypes = this.getKernelSpace().getCorePrototypes();

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

    public <T extends APrototype> void deleteByName(long spaceType, Class<T> clazz, String name) {
        this.containByName(spaceType, clazz, name);

        if (StringUtil.isNameIllegal(name)) {
            name = clazz.getName();
        }

        if (spaceType == SpaceType.KERNEL) {
            Lock lock = this.getLock(spaceType, LockType.WRITE);

            try {
                lock.lock();

                Map<String, UUID> namedCorePrototypeIDs = this.getKernelSpace().getNamedCorePrototypeIDs();

                UUID id = namedCorePrototypeIDs.getOrDefault(name, null);
                if (ValueUtil.isAnyNullOrEmpty(id)) {
                    throw new StatusNotExistedException();
                }

                Map<UUID, APrototype> corePrototypes = this.getKernelSpace().getCorePrototypes();

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

    public <T extends APrototype> void deleteByID(long spaceType, Class<T> clazz, UUID id) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceType.KERNEL) {
            Lock lock = this.getLock(spaceType, LockType.WRITE);

            try {
                lock.lock();

                Map<String, UUID> namedCorePrototypeIDs = this.getKernelSpace().getNamedCorePrototypeIDs();
                Map<Class<? extends APrototype>, UUID> classedCorePrototypeIDs =
                        this.getKernelSpace().getClassedCorePrototypeIDs();

                for (Entry<String, UUID> namedCorePrototypeID : namedCorePrototypeIDs.entrySet()) {
                    if (namedCorePrototypeID.getValue() == id) {
                        namedCorePrototypeIDs.remove(namedCorePrototypeID.getKey());
                        break;
                    }
                }
                for (Entry<Class<? extends APrototype>, UUID> classedCorePrototypeID : classedCorePrototypeIDs.entrySet()) {
                    if (classedCorePrototypeID.getValue() == id) {
                        classedCorePrototypeIDs.remove(classedCorePrototypeID.getKey());
                        break;
                    }
                }

                Map<UUID, APrototype> corePrototypes = this.getKernelSpace().getCorePrototypes();

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
