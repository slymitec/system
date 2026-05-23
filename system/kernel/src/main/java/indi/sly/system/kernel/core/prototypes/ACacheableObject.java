package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.lang.StatusAlreadyFinishedException;
import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.values.ACacheEntity;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.CacheRepositoryObject;

import java.util.UUID;

public class ACacheableObject<T extends ACacheEntity> extends AObject {
    protected T cache;

    public final T getCache() {
        return this.cache;
    }

    public final void setCache(T cache) {
        this.cache = cache;
    }

    public final UUID getHandle() {
        if (ObjectUtil.isAnyNull(this.cache, this.cache.getId())) {
            throw new StatusNotSupportedException();
        }

        return this.cache.getId();
    }

    public final UUID cache() {
        if (ObjectUtil.isAnyNull(this.cache)) {
            throw new StatusNotSupportedException();
        }
        if (!ValueUtil.isAnyNullOrEmpty(this.cache.getId())) {
            throw new StatusAlreadyFinishedException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();

        cacheRepository.add(this.cache);

        return this.cache.getId();
    }

    public final void uncache() {
        if (ObjectUtil.isAnyNull(this.cache)) {
            throw new StatusNotSupportedException();
        }
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getId())) {
            throw new StatusAlreadyFinishedException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();

        UUID id = this.cache.getId();

        cacheRepository.delete(cache.getClass(), id);

        this.cache.setId(null);
    }
}
