package indi.sly.system.kernel.memory.caches;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import javax.inject.Named;

import indi.sly.system.common.types.LockTypes;
import indi.sly.system.kernel.core.CoreObjectRepositoryObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import indi.sly.system.common.utility.LogicalUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.ACoreObject;
import indi.sly.system.kernel.core.enviroment.SpaceTypes;
import indi.sly.system.kernel.objects.prototypes.InfoObject;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoObjectCacheObject extends ACoreObject {
    private Map<UUID, InfoObjectCacheDefinition> getKernelObjectCaches(long spaceType) {
        if (spaceType == SpaceTypes.KERNEL) {
            return this.factoryManager.getKernelSpace().getCachedKernelObjectDefinitions();
        } else if (spaceType == SpaceTypes.USER) {
            return this.factoryManager.getUserSpace().getCachedKernelObjectDefinitions();
        } else {
            Map<UUID, InfoObjectCacheDefinition> kernelObjectCache = new HashMap<>();

            kernelObjectCache.putAll(this.factoryManager.getKernelSpace().getCachedKernelObjectDefinitions());
            kernelObjectCache.putAll(this.factoryManager.getUserSpace().getCachedKernelObjectDefinitions());

            return kernelObjectCache;
        }
    }

    private InfoObject getIfExistedBySpaceType(long spaceType, UUID id) {
        CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();
        Lock lock = coreObjectRepository.getLock(spaceType, LockTypes.READ);

        try {
            lock.lock();

            InfoObjectCacheDefinition kernelObjectCache = this.getKernelObjectCaches(spaceType).getOrDefault(id, null);

            InfoObject infoObject = null;

            if (ObjectUtils.allNotNull(kernelObjectCache)) {
                infoObject = coreObjectRepository.getByID(spaceType, InfoObject.class, id);
            }

            return infoObject;
        } finally {
            lock.unlock();
        }
    }

    private void addBySpaceType(long spaceType, InfoObject infoObject) {
        CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();
        Lock lock = coreObjectRepository.getLock(spaceType, LockTypes.WRITE);

        try {
            lock.lock();

            Map<UUID, InfoObjectCacheDefinition> kernelObjectCaches = this.getKernelObjectCaches(spaceType);
            if (!kernelObjectCaches.containsKey(infoObject.getID())) {
                InfoObjectCacheDefinition kernelObjectCache = new InfoObjectCacheDefinition();
                kernelObjectCache.setId(infoObject.getID());
                kernelObjectCache.setType(infoObject.getType());

                this.getKernelObjectCaches(spaceType).put(kernelObjectCache.getId(), kernelObjectCache);
                coreObjectRepository.addByID(spaceType, kernelObjectCache.getId(), infoObject);
            }
        } finally {
            lock.unlock();
        }
    }

    private void deleteBySpaceType(long spaceType, UUID id) {
        CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();
        Lock lock = coreObjectRepository.getLock(spaceType, LockTypes.WRITE);

        try {
            lock.lock();

            Map<UUID, InfoObjectCacheDefinition> kernelObjectCaches = this.getKernelObjectCaches(spaceType);
            if (kernelObjectCaches.containsKey(id)) {
                kernelObjectCaches.remove(id);

                coreObjectRepository.deleteByID(spaceType, InfoObject.class, id);
            }
        } finally {
            lock.unlock();
        }
    }

    public Map<UUID, InfoObjectCacheDefinition> list(long spaceType) {
        return Collections.unmodifiableMap(this.getKernelObjectCaches(spaceType));
    }

    public InfoObject getIfExisted(long spaceType, UUID id) {
        InfoObject infoObject = null;

        if (LogicalUtils.isAnyExist(spaceType, SpaceTypes.USER)) {
            infoObject = this.getIfExistedBySpaceType(SpaceTypes.USER, id);
        }
        if (LogicalUtils.isAnyExist(spaceType, SpaceTypes.KERNEL)) {
            infoObject = this.getIfExistedBySpaceType(SpaceTypes.KERNEL, id);
        }

        return infoObject;
    }

    public void add(long spaceType, InfoObject infoObject) {
        if (LogicalUtils.isAnyExist(spaceType, SpaceTypes.KERNEL)) {
            this.addBySpaceType(SpaceTypes.KERNEL, infoObject);
        }
        if (LogicalUtils.isAnyExist(spaceType, SpaceTypes.USER)) {
            this.addBySpaceType(SpaceTypes.USER, infoObject);
        }
    }

    public void delete(long spaceType, UUID id) {
        if (LogicalUtils.isAnyExist(spaceType, SpaceTypes.KERNEL)) {
            this.deleteBySpaceType(SpaceTypes.KERNEL, id);
        }
        if (LogicalUtils.isAnyExist(spaceType, SpaceTypes.USER)) {
            this.deleteBySpaceType(SpaceTypes.USER, id);
        }
    }
}
