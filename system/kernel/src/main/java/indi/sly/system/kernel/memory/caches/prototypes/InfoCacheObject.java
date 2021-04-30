package indi.sly.system.kernel.memory.caches.prototypes;

import java.util.*;
import java.util.concurrent.locks.Lock;

import javax.inject.Named;

import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeTypes;
import indi.sly.system.kernel.core.prototypes.CoreRepositoryObject;
import indi.sly.system.kernel.memory.caches.values.InfoCacheDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.objects.prototypes.InfoObject;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoCacheObject extends APrototype {
    private Map<UUID, InfoCacheDefinition> getInfoCaches(long spaceType) {
        if (spaceType == SpaceType.KERNEL) {
            return this.factoryManager.getKernelSpace().getInfoCaches();
        } else if (spaceType == SpaceType.USER) {
            return this.factoryManager.getUserSpace().getCachedInfoObjectDefinitions();
        } else {
            Map<UUID, InfoCacheDefinition> kernelObjectCache = new HashMap<>();

            kernelObjectCache.putAll(this.factoryManager.getKernelSpace().getInfoCaches());
            kernelObjectCache.putAll(this.factoryManager.getUserSpace().getCachedInfoObjectDefinitions());

            return kernelObjectCache;
        }
    }

    private InfoObject getIfExistedBySpaceType(long spaceType, UUID id) {
        CoreRepositoryObject coreRepository = this.factoryManager.getCoreRepository();
        Lock lock = coreRepository.getLock(spaceType, LockType.READ);
        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);

        try {
            lock.lock();

            InfoCacheDefinition kernelObjectCache = this.getInfoCaches(spaceType).getOrDefault(id, null);
            InfoObject infoObject = null;

            if (ObjectUtil.allNotNull(kernelObjectCache)) {
                kernelObjectCache.getDate().put(DateTimeTypes.ACCESS, dateTime.getCurrentDateTime());

                infoObject = coreRepository.getByID(spaceType, InfoObject.class, id);
            }

            return infoObject;
        } finally {
            lock.unlock();
        }
    }

    private void addBySpaceType(long spaceType, InfoObject infoObject) {
        CoreRepositoryObject coreRepository = this.factoryManager.getCoreRepository();
        Lock lock = coreRepository.getLock(spaceType, LockType.WRITE);
        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);

        try {
            lock.lock();

            Map<UUID, InfoCacheDefinition> kernelObjectCaches = this.getInfoCaches(spaceType);
            if (!kernelObjectCaches.containsKey(infoObject.getID())) {
                InfoCacheDefinition kernelObjectCache = new InfoCacheDefinition();
                kernelObjectCache.setId(infoObject.getID());
                kernelObjectCache.setType(infoObject.getType());
                kernelObjectCache.getDate().put(DateTimeTypes.CREATE, dateTime.getCurrentDateTime());
                kernelObjectCache.getDate().put(DateTimeTypes.ACCESS, dateTime.getCurrentDateTime());

                this.getInfoCaches(spaceType).put(kernelObjectCache.getId(), kernelObjectCache);
                coreRepository.addByID(spaceType, kernelObjectCache.getId(), infoObject);
            }
        } finally {
            lock.unlock();
        }
    }

    public Map<UUID, InfoCacheDefinition> list(long spaceType) {
        return Collections.unmodifiableMap(this.getInfoCaches(spaceType));
    }

    public InfoObject getIfExisted(long spaceType, UUID id) {
        InfoObject infoObject = null;

        if (LogicalUtil.isAnyExist(spaceType, SpaceType.USER)) {
            infoObject = this.getIfExistedBySpaceType(SpaceType.USER, id);
        }
        if (LogicalUtil.isAnyExist(spaceType, SpaceType.KERNEL)) {
            infoObject = this.getIfExistedBySpaceType(SpaceType.KERNEL, id);
        }

        return infoObject;
    }

    public void add(long spaceType, InfoObject infoObject) {
        if (LogicalUtil.isAnyExist(spaceType, SpaceType.KERNEL)) {
            this.addBySpaceType(SpaceType.KERNEL, infoObject);
        }
        if (LogicalUtil.isAnyExist(spaceType, SpaceType.USER)) {
            this.addBySpaceType(SpaceType.USER, infoObject);
        }
    }

    public void delete(long spaceType, UUID id) {
        if (LogicalUtil.isAnyExist(spaceType, SpaceType.KERNEL)) {
            this.deleteBySpaceType(SpaceType.KERNEL, id);
        }
        if (LogicalUtil.isAnyExist(spaceType, SpaceType.USER)) {
            this.deleteBySpaceType(SpaceType.USER, id);
        }
    }

    public void deleteIfExpired(long spaceType) {
        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
        long expiredTime =
                this.factoryManager.getKernelSpace().getConfiguration().MEMORY_CACHES_USERSPACE_INFOOBJECT_EXPIRED_TIME;

        if (LogicalUtil.isAnyExist(spaceType, SpaceType.USER)) {
            Map<UUID, InfoCacheDefinition> infoObjectCaches = this.getInfoCaches(SpaceType.USER);

            for (Map.Entry<UUID, InfoCacheDefinition> infoObjectCache : infoObjectCaches.entrySet()) {
                if (dateTime.getCurrentDateTime() - infoObjectCache.getValue().getDate().get(DateTimeTypes.ACCESS) > expiredTime) {
                    this.deleteBySpaceType(SpaceType.USER, infoObjectCache.getKey());
                }
            }
        }
    }

    private void deleteBySpaceType(long spaceType, UUID id) {
        CoreRepositoryObject coreRepository = this.factoryManager.getCoreRepository();
        Lock lock = coreRepository.getLock(spaceType, LockType.WRITE);

        try {
            lock.lock();

            Map<UUID, InfoCacheDefinition> kernelObjectCaches = this.getInfoCaches(spaceType);
            if (kernelObjectCaches.containsKey(id)) {
                kernelObjectCaches.remove(id);

                coreRepository.deleteByID(spaceType, InfoObject.class, id);
            }
        } finally {
            lock.unlock();
        }
    }
}
