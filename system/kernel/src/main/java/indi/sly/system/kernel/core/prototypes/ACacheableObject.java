package indi.sly.system.kernel.core.prototypes;

import com.github.f4b6a3.ulid.Ulid;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyFinishedException;
import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.enviroment.values.CacheDurationType;
import indi.sly.system.kernel.core.values.ACacheEntity;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ACacheRepositoryObject;

import java.time.Duration;
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

        return this.cache.getId().toUuid();
    }

    public final UUID cache(long duration) {
        if (ObjectUtil.isAnyNull(this.cache)) {
            throw new StatusNotSupportedException();
        }
        if (!ValueUtil.isAnyNullOrEmpty(this.cache.getId())) {
            throw new StatusAlreadyFinishedException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        ACacheRepositoryObject<T> objectRepository = memoryManager.getCacheRepository(this.cache.getCacheRepositoryId());

        objectRepository.add(this.cache);

        return this.cache.getId().toUuid();
    }

    public final void uncache() {
        if (ObjectUtil.isAnyNull(this.cache)) {
            throw new StatusNotSupportedException();
        }
        if (ValueUtil.isAnyNullOrEmpty(this.cache.getId())) {
            throw new StatusAlreadyFinishedException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        ACacheRepositoryObject<T> objectRepository = memoryManager.getCacheRepository(this.cache.getCacheRepositoryId());

        UUID id = this.cache.getId().toUuid();

        objectRepository.delete(id);

        this.cache.setId(null);
    }
}
