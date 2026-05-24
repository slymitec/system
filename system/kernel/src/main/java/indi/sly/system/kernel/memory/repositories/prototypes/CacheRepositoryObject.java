package indi.sly.system.kernel.memory.repositories.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.enviroment.values.CacheDurationType;
import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.core.values.ACacheEntity;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.inject.Named;
import org.redisson.api.RLiveObject;
import org.redisson.api.RLiveObjectService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.time.Duration;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CacheRepositoryObject extends AObject {
    @Resource
    private RedissonClient redissonClient;

    private RLiveObjectService liveObjectService;

    @PostConstruct
    public void init() {
        this.liveObjectService = this.redissonClient.getLiveObjectService();
    }

    public <T extends ACacheEntity> boolean contain(Class<T> clazz, UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        ACacheEntity cache = this.liveObjectService.get(clazz, id);

        return !ObjectUtil.isNull(cache);
    }

    public <T extends ACacheEntity> T get(Class<T> clazz, UUID id) {
        if (ObjectUtil.isAnyNull(clazz) || ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        T cache = this.liveObjectService.get(clazz, id);

        if (ObjectUtil.isNull(cache)) {
            throw new StatusNotExistedException();
        } else {
            return cache;
        }
    }

    public <T extends ACacheEntity> T add(T cache) {
        if (ObjectUtil.isAnyNull(cache)) {
            throw new ConditionParametersException();
        }

        if (ValueUtil.isAnyNullOrEmpty(cache.getId())) {
            cache.setId(UUIDUtil.createRandom());
        }

        cache = this.liveObjectService.persist(cache);

        RLiveObject liveObject = this.liveObjectService.asLiveObject(cache);

        long duration = cache.getDuration();
        if (duration == CacheDurationType.INSTANT) {
            liveObject.expire(Duration.ofSeconds(4L));
        } else if (duration == CacheDurationType.SHORT) {
            liveObject.expire(Duration.ofSeconds(8L));
        } else if (duration == CacheDurationType.NORMAL) {
            liveObject.expire(Duration.ofSeconds(16L));
        } else if (duration == CacheDurationType.LONG) {
            liveObject.expire(Duration.ofSeconds(32L));
        } else if (duration == CacheDurationType.AGES) {
            liveObject.expire(Duration.ofSeconds(64L));
        } else {
            liveObject.expire(Duration.ofSeconds(128L));
        }

        return cache;
    }

    public <T extends ACacheEntity> void refresh(Class<T> clazz, T cache) {
        if (ObjectUtil.isAnyNull(clazz, cache)) {
            throw new ConditionParametersException();
        }
        if (ValueUtil.isAnyNullOrEmpty(cache.getId())) {
            return;
        }

        if (!this.liveObjectService.isLiveObject(cache)) {
            cache = this.liveObjectService.attach(cache);
        }
        if (ObjectUtil.isNull(cache)) {
            throw new StatusNotExistedException();
        }

        RLiveObject liveObject = this.liveObjectService.asLiveObject(cache);

        long duration = cache.getDuration();
        if (duration == CacheDurationType.INSTANT) {
            liveObject.expire(Duration.ofSeconds(4L));
        } else if (duration == CacheDurationType.SHORT) {
            liveObject.expire(Duration.ofSeconds(8L));
        } else if (duration == CacheDurationType.NORMAL) {
            liveObject.expire(Duration.ofSeconds(16L));
        } else if (duration == CacheDurationType.LONG) {
            liveObject.expire(Duration.ofSeconds(32L));
        } else if (duration == CacheDurationType.AGES) {
            liveObject.expire(Duration.ofSeconds(64L));
        } else {
            liveObject.expire(Duration.ofSeconds(128L));
        }
    }

    public <T extends ACacheEntity> void update(T cache) {
        if (ValueUtil.isAnyNullOrEmpty(cache, cache.getId())) {
            throw new ConditionParametersException();
        }

        cache = this.liveObjectService.merge(cache);

        if (ObjectUtil.isNull(cache)) {
            throw new StatusNotExistedException();
        }

        RLiveObject liveObject = this.liveObjectService.asLiveObject(cache);

        long duration = cache.getDuration();
        if (duration == CacheDurationType.INSTANT) {
            liveObject.expire(Duration.ofSeconds(4L));
        } else if (duration == CacheDurationType.SHORT) {
            liveObject.expire(Duration.ofSeconds(8L));
        } else if (duration == CacheDurationType.NORMAL) {
            liveObject.expire(Duration.ofSeconds(16L));
        } else if (duration == CacheDurationType.LONG) {
            liveObject.expire(Duration.ofSeconds(32L));
        } else if (duration == CacheDurationType.AGES) {
            liveObject.expire(Duration.ofSeconds(64L));
        } else {
            liveObject.expire(Duration.ofSeconds(128L));
        }
    }

    public <T extends ACacheEntity> void delete(Class<T> clazz, UUID id) {
        if (ObjectUtil.isAnyNull(clazz) || ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        this.liveObjectService.delete(clazz, id);
    }
}
