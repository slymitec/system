package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.lang.StatusAlreadyFinishedException;
import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.environment.values.CacheDurationType;
import indi.sly.system.kernel.core.values.ACacheEntity;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.CacheRepositoryObject;

import java.util.UUID;

public class ACacheableObject<T extends ACacheEntity> extends AObject {
    @SuppressWarnings("NullAway.Init")
    protected T cache;

    public final T getCache() {
        if (ObjectUtil.isNull(this.cache)) {
            throw new StatusNotSupportedException();
        }

        return this.cache;
    }

    public final void setCache(T cache) {
        if (ObjectUtil.isNull(cache)) {
            throw new ConditionParametersException();
        }

        this.cache = cache;
    }

    public final UUID getHandle() {
        if (ObjectUtil.isNull(this.cache) || ValueUtil.isNullOrEmpty(this.cache.getId())) {
            throw new StatusNotSupportedException();
        }

        return this.cache.getId();
    }

    public final UUID cache() {
        if (ObjectUtil.isNull(this.cache)) {
            throw new StatusNotSupportedException();
        }
        if (!ValueUtil.isNullOrEmpty(this.cache.getId())) {
            throw new StatusAlreadyFinishedException();
        }

        if (ValueUtil.isNullOrEmpty(cache.getId())) {
            cache.setId(UUIDUtil.createRandom());
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();

        this.cache = cacheRepository.add(this.cache);

        return this.cache.getId();
    }

    public final void uncache() {
        if (ObjectUtil.isNull(this.cache)) {
            throw new StatusNotSupportedException();
        }
        if (ValueUtil.isNullOrEmpty(this.cache.getId())) {
            throw new StatusAlreadyFinishedException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();

        cacheRepository.delete(this.cache);

        this.cache = null;
    }

    public final void expire(long duration) {
        if (ObjectUtil.isNull(this.cache)) {
            throw new StatusNotSupportedException();
        }
        if (ValueUtil.isNullOrEmpty(this.cache.getId())) {
            throw new StatusAlreadyFinishedException();
        }

        if (LogicalUtil.isAnyEqual(duration, CacheDurationType.PERMANENT)) {
            throw new ConditionRefuseException();
        }
        this.cache.setDuration(duration);

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();

        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) cache.getClass();
        cacheRepository.refresh(clazz, this.cache);
    }
}
