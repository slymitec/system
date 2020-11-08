package indi.sly.system.kernel.memory.caches.prototypes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import javax.inject.Named;

import indi.sly.system.common.types.LockTypes;
import indi.sly.system.kernel.core.prototypes.CoreObjectRepositoryObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import indi.sly.system.common.utility.LogicalUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.core.enviroment.SpaceTypes;
import indi.sly.system.kernel.objects.prototypes.InfoObject;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoObjectCacheObject extends ACoreObject {
    private Map<UUID, InfoObjectCacheDefinition> getInfoObjectCaches(long spaceType) {
        if (spaceType == SpaceTypes.KERNEL) {
            return this.factoryManager.getKernelSpace().getCachedInfoObjectDefinitions();
        } else if (spaceType == SpaceTypes.USER) {
            return this.factoryManager.getUserSpace().getCachedInfoObjectDefinitions();
        } else {
            Map<UUID, InfoObjectCacheDefinition> kernelObjectCache = new HashMap<>();

            kernelObjectCache.putAll(this.factoryManager.getKernelSpace().getCachedInfoObjectDefinitions());
            kernelObjectCache.putAll(this.factoryManager.getUserSpace().getCachedInfoObjectDefinitions());

            return kernelObjectCache;
        }
    }

    private InfoObject getIfExistedBySpaceType(long spaceType, UUID id) {
        CoreObjectRepositoryObject coreObjectRepository = this.factoryManager.getCoreObjectRepository();
        Lock lock = coreObjectRepository.getLock(spaceType, LockTypes.READ);

        try {
            lock.lock();

            InfoObjectCacheDefinition kernelObjectCache = this.getInfoObjectCaches(spaceType).getOrDefault(id, null);

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

            Map<UUID, InfoObjectCacheDefinition> kernelObjectCaches = this.getInfoObjectCaches(spaceType);
            if (!kernelObjectCaches.containsKey(infoObject.getID())) {
                InfoObjectCacheDefinition kernelObjectCache = new InfoObjectCacheDefinition();
                kernelObjectCache.setId(infoObject.getID());
                kernelObjectCache.setType(infoObject.getType());

                this.getInfoObjectCaches(spaceType).put(kernelObjectCache.getId(), kernelObjectCache);
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

            Map<UUID, InfoObjectCacheDefinition> kernelObjectCaches = this.getInfoObjectCaches(spaceType);
            if (kernelObjectCaches.containsKey(id)) {
                kernelObjectCaches.remove(id);

                coreObjectRepository.deleteByID(spaceType, InfoObject.class, id);
            }
        } finally {
            lock.unlock();
        }
    }

    public Map<UUID, InfoObjectCacheDefinition> list(long spaceType) {
        return Collections.unmodifiableMap(this.getInfoObjectCaches(spaceType));
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
