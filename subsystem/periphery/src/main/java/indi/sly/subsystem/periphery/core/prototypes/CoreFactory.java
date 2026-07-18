package indi.sly.subsystem.periphery.core.prototypes;

import indi.sly.subsystem.periphery.core.date.prototypes.DateTimeObject;
import indi.sly.subsystem.periphery.core.environment.values.CacheDurationType;
import indi.sly.subsystem.periphery.core.values.NoneCacheEntity;
import indi.sly.subsystem.periphery.memory.MemoryManager;
import indi.sly.subsystem.periphery.memory.repositories.prototypes.CacheRepositoryObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CoreFactory extends AFactory {
    @Override
    public void init() {
    }

    private DateTimeObject createDateTime(NoneCacheEntity cache) {
        DateTimeObject dateTime = this.coreManager.create(DateTimeObject.class);

        dateTime.setCache(cache);

        return dateTime;
    }

    public DateTimeObject buildDateTime() {
        NoneCacheEntity cache = new NoneCacheEntity();

        cache.setDuration(CacheDurationType.NORMAL);

        return this.createDateTime(cache);
    }

    public DateTimeObject rebuildDateTime(UUID handle) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        NoneCacheEntity cache = cacheRepository.get(NoneCacheEntity.class, handle);

        return this.rebuildDateTime(cache);
    }

    public DateTimeObject rebuildDateTime(NoneCacheEntity cache) {
        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

        CacheRepositoryObject cacheRepository = memoryManager.getCacheRepository();
        cacheRepository.refresh(NoneCacheEntity.class, cache);

        return this.createDateTime(cache);
    }
}
