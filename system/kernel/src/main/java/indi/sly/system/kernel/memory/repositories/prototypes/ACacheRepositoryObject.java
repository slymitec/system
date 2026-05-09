package indi.sly.system.kernel.memory.repositories.prototypes;

import com.github.f4b6a3.ulid.Ulid;
import com.redis.om.spring.repository.RedisDocumentRepository;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.*;
import indi.sly.system.kernel.core.enviroment.values.CacheDurationType;
import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.core.values.ACacheEntity;
import jakarta.annotation.Resource;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.context.ApplicationContext;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class ACacheRepositoryObject<T extends ACacheEntity> extends AObject {
    @Resource
    private RedisDocumentRepository<T, Ulid> redisDocumentRepository;

    @Resource
    private ApplicationContext context;

    public boolean contain(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        Ulid ulid = Ulid.from(id);

        return redisDocumentRepository.existsById(ulid);
    }

    public T get(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        Ulid ulid = Ulid.from(id);

        return this.redisDocumentRepository.findById(ulid).orElseThrow(StatusNotExistedException::new);
    }

    public void add(T object) {
        if (ValueUtil.isAnyNullOrEmpty(object)) {
            throw new ConditionParametersException();
        }

        this.redisDocumentRepository.save(object);

        Ulid ulid = object.getId();
        long duration = object.getDuration();

        if (duration == CacheDurationType.INSTANT) {
            this.redisDocumentRepository.setExpiration(ulid, 4L, TimeUnit.SECONDS);
        } else if (duration == CacheDurationType.SHORT) {
            this.redisDocumentRepository.setExpiration(ulid, 8L, TimeUnit.SECONDS);
        } else if (duration == CacheDurationType.NORMAL) {
            this.redisDocumentRepository.setExpiration(ulid, 16L, TimeUnit.SECONDS);
        } else if (duration == CacheDurationType.LONG) {
            this.redisDocumentRepository.setExpiration(ulid, 32L, TimeUnit.SECONDS);
        } else if (duration == CacheDurationType.AGES) {
            this.redisDocumentRepository.setExpiration(ulid, 64L, TimeUnit.SECONDS);
        } else {
            this.redisDocumentRepository.setExpiration(ulid, 2L, TimeUnit.SECONDS);
        }
    }

    public void refresh(T object) {
        if (ValueUtil.isAnyNullOrEmpty(object)) {
            throw new ConditionParametersException();
        }

        Ulid ulid = object.getId();
        long duration = object.getDuration();

        if (duration == CacheDurationType.INSTANT) {
            this.redisDocumentRepository.setExpiration(ulid, 4L, TimeUnit.SECONDS);
        } else if (duration == CacheDurationType.SHORT) {
            this.redisDocumentRepository.setExpiration(ulid, 8L, TimeUnit.SECONDS);
        } else if (duration == CacheDurationType.NORMAL) {
            this.redisDocumentRepository.setExpiration(ulid, 16L, TimeUnit.SECONDS);
        } else if (duration == CacheDurationType.LONG) {
            this.redisDocumentRepository.setExpiration(ulid, 32L, TimeUnit.SECONDS);
        } else if (duration == CacheDurationType.AGES) {
            this.redisDocumentRepository.setExpiration(ulid, 64L, TimeUnit.SECONDS);
        } else {
            this.redisDocumentRepository.setExpiration(ulid, 2L, TimeUnit.SECONDS);
        }
    }

    public void update(T object) {
        if (ValueUtil.isAnyNullOrEmpty(object) || ObjectUtil.isNull(object.getId())) {
            throw new ConditionParametersException();
        }

        this.redisDocumentRepository.update(object);

        Ulid ulid = object.getId();
        long duration = object.getDuration();

        if (duration == CacheDurationType.INSTANT) {
            this.redisDocumentRepository.setExpiration(ulid, 4L, TimeUnit.SECONDS);
        } else if (duration == CacheDurationType.SHORT) {
            this.redisDocumentRepository.setExpiration(ulid, 8L, TimeUnit.SECONDS);
        } else if (duration == CacheDurationType.NORMAL) {
            this.redisDocumentRepository.setExpiration(ulid, 16L, TimeUnit.SECONDS);
        } else if (duration == CacheDurationType.LONG) {
            this.redisDocumentRepository.setExpiration(ulid, 32L, TimeUnit.SECONDS);
        } else if (duration == CacheDurationType.AGES) {
            this.redisDocumentRepository.setExpiration(ulid, 64L, TimeUnit.SECONDS);
        } else {
            this.redisDocumentRepository.setExpiration(ulid, 2L, TimeUnit.SECONDS);
        }
    }

    public void delete(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        Ulid ulid = Ulid.from(id);

        this.redisDocumentRepository.deleteById(ulid);
    }
}
