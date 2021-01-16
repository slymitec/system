package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CoreRepositoryObject extends APrototype {
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

    public Lock getLock(long spaceType, long lockType) {
        Lock lock = null;

        if (spaceType == SpaceType.KERNEL) {
            if (lockType == LockType.READ) {
                lock = this.getKernelSpace().getCorePrototypeLock().readLock();
            } else if (lockType == LockType.WRITE) {
                lock = this.getKernelSpace().getCorePrototypeLock().writeLock();
            }
        } else if (spaceType == SpaceType.USER) {
            if (lockType == LockType.READ) {
                lock = this.getUserSpace().getInfoObjectLock().readLock();
            } else if (lockType == LockType.WRITE) {
                lock = this.getUserSpace().getInfoObjectLock().writeLock();
            }
        }

        if (ObjectUtil.isAnyNull(lock)) {
            throw new StatusNotSupportedException();
        }

        return lock;
    }

    public Set<APrototype> getAll(long spaceType) {
        Map<UUID, APrototype> corePrototypes;

        if (spaceType == SpaceType.KERNEL) {
            corePrototypes = this.getKernelSpace().getCorePrototypes();
        } else if (spaceType == SpaceType.USER) {
            corePrototypes = new HashMap<>();

            for (Entry<UUID, InfoObject> pair : this.getUserSpace().getInfoObjects().entrySet()) {
                corePrototypes.put(pair.getKey(), pair.getValue());
            }
        } else if (spaceType == SpaceType.ALL) {
            corePrototypes = new HashMap<>(this.getKernelSpace().getCorePrototypes());

            for (Entry<UUID, InfoObject> pair : this.getUserSpace().getInfoObjects().entrySet()) {
                corePrototypes.put(pair.getKey(), pair.getValue());
            }
        } else {
            throw new ConditionParametersException();
        }

        return Collections.unmodifiableSet(new HashSet<>(corePrototypes.values()));
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
        } else if (spaceType == SpaceType.USER) {
            Lock lock = this.getLock(spaceType, LockType.READ);

            try {
                lock.lock();

                Map<UUID, InfoObject> corePrototypes = this.getUserSpace().getInfoObjects();

                InfoObject corePrototype = corePrototypes.getOrDefault(id, null);
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

    public <T> Set<APrototype> getByImplementInterface(long spaceType, Class<T> clazz) {
        if (!clazz.isInterface()) {
            throw new ConditionParametersException();
        }

        if (spaceType == SpaceType.KERNEL) {
            Lock lock = this.getLock(spaceType, LockType.READ);

            try {
                lock.lock();

                Set<APrototype> corePrototypes = this.getAll(spaceType);
                Set<APrototype> resultCorePrototypes = new HashSet<>();

                for (APrototype pair : corePrototypes) {
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
        } else if (spaceType == SpaceType.USER) {
            Lock lock = this.getLock(spaceType, LockType.WRITE);

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
        } else if (spaceType == SpaceType.USER) {
            Lock lock = this.getLock(spaceType, LockType.WRITE);

            try {
                lock.lock();

                Map<UUID, InfoObject> corePrototypes = this.getUserSpace().getInfoObjects();

                InfoObject corePrototype = corePrototypes.getOrDefault(id, null);
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

                for (Entry<String, UUID> pair : namedCorePrototypeIDs.entrySet()) {
                    if (pair.getValue() == id) {
                        namedCorePrototypeIDs.remove(pair.getKey());
                        break;
                    }
                }
                for (Entry<Class<? extends APrototype>, UUID> pair : classedCorePrototypeIDs.entrySet()) {
                    if (pair.getValue() == id) {
                        classedCorePrototypeIDs.remove(pair.getKey());
                        break;
                    }
                }

                Map<UUID, APrototype> corePrototypes = this.getKernelSpace().getCorePrototypes();

                this.getByID(spaceType, clazz, id);

                corePrototypes.remove(id);
            } finally {
                lock.unlock();
            }
        } else if (spaceType == SpaceType.USER) {
            Lock lock = this.getLock(spaceType, LockType.WRITE);

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
