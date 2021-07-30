package indi.sly.system.kernel.memory.caches.prototypes;

import java.util.*;
import java.util.concurrent.locks.Lock;

import javax.inject.Named;

import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
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
    private Map<UUID, InfoCacheDefinition> getInfoCaches(long space) {
        if (space == SpaceType.KERNEL) {
            return this.factoryManager.getKernelSpace().getInfoCaches();
        } else if (space == SpaceType.USER) {
            return this.factoryManager.getUserSpace().getInfoCaches();
        } else {
            Map<UUID, InfoCacheDefinition> kernelObjectCache = new HashMap<>();

            kernelObjectCache.putAll(this.factoryManager.getKernelSpace().getInfoCaches());
            kernelObjectCache.putAll(this.factoryManager.getUserSpace().getInfoCaches());

            return kernelObjectCache;
        }
    }

    private InfoObject getIfExistedBySpace(long space, UUID id) {
        CoreRepositoryObject coreRepository = this.factoryManager.getCoreRepository();
        Lock lock = coreRepository.getLock(space, LockType.READ);
        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);

        try {
            lock.lock();

            InfoCacheDefinition kernelObjectCache = this.getInfoCaches(space).getOrDefault(id, null);
            InfoObject infoObject = null;

            if (ObjectUtil.allNotNull(kernelObjectCache)) {
                kernelObjectCache.getDate().put(DateTimeType.ACCESS, dateTime.getCurrentDateTime());

                infoObject = coreRepository.getByID(space, InfoObject.class, id);
            }

            return infoObject;
        } finally {
            lock.unlock();
        }
    }

    private void addBySpace(long space, InfoObject info) {
        CoreRepositoryObject coreRepository = this.factoryManager.getCoreRepository();
        Lock lock = coreRepository.getLock(space, LockType.WRITE);
        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);

        try {
            lock.lock();

            Map<UUID, InfoCacheDefinition> kernelObjectCaches = this.getInfoCaches(space);
            if (!kernelObjectCaches.containsKey(info.getID())) {
                InfoCacheDefinition kernelObjectCache = new InfoCacheDefinition();
                kernelObjectCache.setID(info.getID());
                kernelObjectCache.setType(info.getType());
                kernelObjectCache.getDate().put(DateTimeType.CREATE, dateTime.getCurrentDateTime());
                kernelObjectCache.getDate().put(DateTimeType.ACCESS, dateTime.getCurrentDateTime());

                this.getInfoCaches(space).put(kernelObjectCache.getID(), kernelObjectCache);
                coreRepository.addByID(space, kernelObjectCache.getID(), info);
            }
        } finally {
            lock.unlock();
        }
    }

    public Map<UUID, InfoCacheDefinition> list(long space) {
        return Collections.unmodifiableMap(this.getInfoCaches(space));
    }

    public InfoObject getIfExisted(long space, UUID id) {
        InfoObject infoObject = null;

        if (LogicalUtil.isAnyExist(space, SpaceType.USER)) {
            infoObject = this.getIfExistedBySpace(SpaceType.USER, id);
        }
        if (LogicalUtil.isAnyExist(space, SpaceType.KERNEL)) {
            infoObject = this.getIfExistedBySpace(SpaceType.KERNEL, id);
        }

        return infoObject;
    }

    public void add(long space, InfoObject info) {
        if (LogicalUtil.isAnyExist(space, SpaceType.KERNEL)) {
            this.addBySpace(SpaceType.KERNEL, info);
        }
        if (LogicalUtil.isAnyExist(space, SpaceType.USER)) {
            this.addBySpace(SpaceType.USER, info);
        }
    }

    public void delete(long space, UUID id) {
        if (LogicalUtil.isAnyExist(space, SpaceType.KERNEL)) {
            this.deleteBySpace(SpaceType.KERNEL, id);
        }
        if (LogicalUtil.isAnyExist(space, SpaceType.USER)) {
            this.deleteBySpace(SpaceType.USER, id);
        }
    }

    public void deleteIfExpired(long space) {
        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
        long expiredTime =
                this.factoryManager.getKernelSpace().getConfiguration().MEMORY_CACHES_USERSPACE_INFOOBJECT_EXPIRED_TIME;

        if (LogicalUtil.isAnyExist(space, SpaceType.USER)) {
            Map<UUID, InfoCacheDefinition> infoObjectCaches = this.getInfoCaches(SpaceType.USER);

            for (Map.Entry<UUID, InfoCacheDefinition> infoObjectCache : infoObjectCaches.entrySet()) {
                if (dateTime.getCurrentDateTime() - infoObjectCache.getValue().getDate().get(DateTimeType.ACCESS) > expiredTime) {
                    this.deleteBySpace(SpaceType.USER, infoObjectCache.getKey());
                }
            }
        }
    }

    private void deleteBySpace(long space, UUID id) {
        CoreRepositoryObject coreRepository = this.factoryManager.getCoreRepository();
        Lock lock = coreRepository.getLock(space, LockType.WRITE);

        try {
            lock.lock();

            Map<UUID, InfoCacheDefinition> kernelObjectCaches = this.getInfoCaches(space);
            if (kernelObjectCaches.containsKey(id)) {
                kernelObjectCaches.remove(id);

                coreRepository.deleteByID(space, InfoObject.class, id);
            }
        } finally {
            lock.unlock();
        }
    }
}
