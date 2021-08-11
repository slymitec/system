package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.*;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.enviroment.values.ASpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CorePrototypeRepositoryObject extends APrototype {
    private ASpaceDefinition<?> getSpace(long space) {
        if (space == SpaceType.KERNEL) {
            return this.factoryManager.getKernelSpace();
        } else if (space == SpaceType.USER) {
            return this.factoryManager.getUserSpace();
        } else {
            throw new ConditionParametersException();
        }
    }

    public Lock getLock(long space, long lock) {
        ASpaceDefinition<?> aSpace = this.getSpace(space);

        ReadWriteLock corePrototypeLock = aSpace.getCorePrototypeLock();

        if (ObjectUtil.isAnyNull(corePrototypeLock)) {
            throw new StatusNotSupportedException();
        }

        Lock readWriteLock = null;
        if (lock == LockType.READ) {
            readWriteLock = corePrototypeLock.readLock();
        } else if (lock == LockType.WRITE) {
            readWriteLock = corePrototypeLock.writeLock();
        }

        if (ObjectUtil.isAnyNull(readWriteLock)) {
            throw new StatusNotSupportedException();
        }

        return readWriteLock;
    }

    public Set<APrototype> getAll(long space) {
        ASpaceDefinition<?> aSpace = this.getSpace(space);

        Set<APrototype> corePrototypes = new HashSet<>(aSpace.getCorePrototypes().values());

        return CollectionUtil.unmodifiable(corePrototypes);
    }

    public <T extends APrototype> T get(long spaceType, Class<T> clazz) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        Lock lock = this.getLock(spaceType, LockType.READ);

        try {
            lock.lock();

            UUID id = this.getIDByClass(spaceType, clazz);
            T corePrototype = this.getByID(spaceType, clazz, id);

            return corePrototype;
        } finally {
            lock.unlock();
        }
    }

    public <T extends APrototype> T getByName(long spaceType, Class<T> clazz, String name) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }
        if (StringUtil.isNameIllegal(name)) {
            name = clazz.getName();
        }

        Lock lock = this.getLock(spaceType, LockType.READ);

        try {
            lock.lock();

            UUID id = this.getIDByName(spaceType, name);
            T corePrototype = this.getByID(spaceType, clazz, id);

            return corePrototype;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends APrototype> T getByID(long space, Class<T> clazz, UUID id) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        ASpaceDefinition<?> aSpace = this.getSpace(space);
        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            Map<UUID, APrototype> corePrototypes = aSpace.getCorePrototypes();

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
    }

    public <T extends APrototype> UUID getIDByName(long space, String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        ASpaceDefinition<?> aSpace = this.getSpace(space);
        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            Map<String, UUID> namedCorePrototypeIDs = aSpace.getNamedCorePrototypeIDs();

            UUID id = namedCorePrototypeIDs.getOrDefault(name, null);
            if (ValueUtil.isAnyNullOrEmpty(id)) {
                throw new StatusNotExistedException();
            }

            return id;
        } finally {
            lock.unlock();
        }
    }

    public <T extends APrototype> UUID getIDByClass(long space, Class<T> clazz) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        ASpaceDefinition<?> aSpace = this.getSpace(space);
        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            Map<Class<? extends APrototype>, UUID> classedCorePrototypeIDs = aSpace.getClassedCorePrototypeIDs();

            UUID id = classedCorePrototypeIDs.getOrDefault(clazz, null);
            if (ValueUtil.isAnyNullOrEmpty(id)) {
                throw new StatusNotExistedException();
            }

            return id;
        } finally {
            lock.unlock();
        }
    }

    public <T> Set<APrototype> getByImplementInterface(long space, Class<T> clazz) {
        if (!clazz.isInterface()) {
            throw new ConditionParametersException();
        }

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
    }

    public <T extends APrototype> void add(long spaceType, T corePrototype) {
        this.add(spaceType, null, null, corePrototype);
    }

    public <T extends APrototype> void add(long space, UUID id, String name, T corePrototype) {
        if (ValueUtil.isAnyNullOrEmpty(id) && !StringUtil.isNameIllegal(name)) {
            this.addByName(space, name, corePrototype);
        } else if (!ValueUtil.isAnyNullOrEmpty(id) && StringUtil.isNameIllegal(name)) {
            this.addByID(space, id, corePrototype);
        }
        if (ObjectUtil.isAnyNull(corePrototype)) {
            throw new ConditionParametersException();
        }

        corePrototype.factoryManager = this.factoryManager;

        ASpaceDefinition<?> aSpace = this.getSpace(space);
        Lock lock = this.getLock(space, LockType.WRITE);

        try {
            lock.lock();

            Map<UUID, APrototype> corePrototypes = aSpace.getCorePrototypes();

            id = UUIDUtil.createRandom();
            Class<? extends APrototype> clazz = corePrototype.getClass();

            Map<Class<? extends APrototype>, UUID> classedCorePrototypeIDs = aSpace.getClassedCorePrototypeIDs();

            if (classedCorePrototypeIDs.containsKey(clazz)) {
                throw new StatusAlreadyExistedException();
            }
            classedCorePrototypeIDs.put(clazz, id);

            corePrototypes.put(id, corePrototype);
        } finally {
            lock.unlock();
        }
    }

    public <T extends APrototype> void addByName(long space, String name, T corePrototype) {
        if (ObjectUtil.isAnyNull(corePrototype)) {
            throw new ConditionParametersException();
        }
        if (StringUtil.isNameIllegal(name)) {
            name = corePrototype.getClass().getName();
        }

        corePrototype.factoryManager = this.factoryManager;

        ASpaceDefinition<?> aSpace = this.getSpace(space);
        Lock lock = this.getLock(space, LockType.WRITE);

        try {
            lock.lock();

            Map<UUID, APrototype> corePrototypes = aSpace.getCorePrototypes();

            UUID id = UUIDUtil.createRandom();

            Map<String, UUID> namedCorePrototypeIDs = aSpace.getNamedCorePrototypeIDs();

            if (namedCorePrototypeIDs.containsKey(name)) {
                throw new StatusAlreadyExistedException();
            }
            namedCorePrototypeIDs.put(name, id);

            corePrototypes.put(id, corePrototype);
        } finally {
            lock.unlock();
        }
    }

    public <T extends APrototype> void addByID(long space, UUID id, T corePrototype) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }
        if (ObjectUtil.isAnyNull(corePrototype)) {
            throw new ConditionParametersException();
        }

        corePrototype.factoryManager = this.factoryManager;

        ASpaceDefinition<?> aSpace = this.getSpace(space);
        Lock lock = this.getLock(space, LockType.WRITE);

        try {
            lock.lock();

            Map<UUID, APrototype> corePrototypes = aSpace.getCorePrototypes();

            if (corePrototypes.containsKey(id)) {
                throw new StatusAlreadyExistedException();
            }

            corePrototypes.put(id, corePrototype);
        } finally {
            lock.unlock();
        }
    }

    public <T extends APrototype> boolean contain(long space, Class<T> clazz) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        ASpaceDefinition<?> aSpace = this.getSpace(space);
        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            Map<Class<? extends APrototype>, UUID> classedCorePrototypeIDs = aSpace.getClassedCorePrototypeIDs();

            UUID id = classedCorePrototypeIDs.getOrDefault(clazz, null);
            boolean isContain = this.containByID(space, clazz, id);

            return isContain;
        } finally {
            lock.unlock();
        }
    }

    public <T extends APrototype> boolean containByName(long space, Class<T> clazz, String name) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }
        if (StringUtil.isNameIllegal(name)) {
            name = clazz.getName();
        }

        ASpaceDefinition<?> aSpace = this.getSpace(space);
        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            Map<String, UUID> namedCorePrototypeIDs = aSpace.getNamedCorePrototypeIDs();

            UUID id = namedCorePrototypeIDs.getOrDefault(name, null);
            boolean isContain = this.containByID(space, clazz, id);

            return isContain;
        } finally {
            lock.unlock();
        }
    }

    public <T extends APrototype> boolean containByID(long space, Class<T> clazz, UUID id) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            return false;
        }

        ASpaceDefinition<?> aSpace = this.getSpace(space);
        Lock lock = this.getLock(space, LockType.READ);

        try {
            lock.lock();

            Map<UUID, APrototype> corePrototypes = aSpace.getCorePrototypes();

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
    }

    public <T extends APrototype> void delete(long space, Class<T> clazz) {
        ASpaceDefinition<?> aSpace = this.getSpace(space);
        Lock lock = this.getLock(space, LockType.WRITE);

        try {
            lock.lock();

            Map<Class<? extends APrototype>, UUID> classedCorePrototypeIDs = aSpace.getClassedCorePrototypeIDs();
            Map<UUID, APrototype> corePrototypes = aSpace.getCorePrototypes();

            UUID id = this.getIDByClass(space, clazz);
            this.getByID(space, clazz, id);

            corePrototypes.remove(id);
            classedCorePrototypeIDs.remove(clazz);
        } finally {
            lock.unlock();
        }
    }

    public <T extends APrototype> void deleteByName(long space, Class<T> clazz, String name) {
        if (!this.containByName(space, clazz, name)) {
            throw new StatusNotExistedException();
        }
        if (StringUtil.isNameIllegal(name)) {
            name = clazz.getName();
        }

        ASpaceDefinition<?> aSpace = this.getSpace(space);
        Lock lock = this.getLock(space, LockType.WRITE);

        try {
            lock.lock();

            Map<String, UUID> namedCorePrototypeIDs = aSpace.getNamedCorePrototypeIDs();

            UUID id = namedCorePrototypeIDs.getOrDefault(name, null);
            if (ValueUtil.isAnyNullOrEmpty(id)) {
                throw new StatusNotExistedException();
            }

            Map<UUID, APrototype> corePrototypes = aSpace.getCorePrototypes();

            this.getByID(space, clazz, id);

            corePrototypes.remove(id);
            namedCorePrototypeIDs.remove(name);
        } finally {
            lock.unlock();
        }
    }

    public <T extends APrototype> void deleteByID(long space, Class<T> clazz, UUID id) {
        if (!this.containByID(space, clazz, id)) {
            throw new StatusNotExistedException();
        }

        ASpaceDefinition<?> aSpace = this.getSpace(space);
        Lock lock = this.getLock(space, LockType.WRITE);

        try {
            lock.lock();

            Map<String, UUID> namedCorePrototypeIDs = aSpace.getNamedCorePrototypeIDs();
            Map<Class<? extends APrototype>, UUID> classedCorePrototypeIDs = aSpace.getClassedCorePrototypeIDs();

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

            Map<UUID, APrototype> corePrototypes = aSpace.getCorePrototypes();

            this.getByID(space, clazz, id);

            corePrototypes.remove(id);
        } finally {
            lock.unlock();
        }
    }
}
