package indi.sly.system.kernel.memory.repositories.prototypes;

import com.github.f4b6a3.ulid.Ulid;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.AObject;
import jakarta.annotation.Resource;
import jakarta.inject.Named;
import org.redisson.api.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CommunicationRepositoryObject extends AObject {
    @Resource
    private RedissonClient redissonClient;

    public RLock getLock(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        return this.redissonClient.getLock(Ulid.from(id).toString());
    }

    public RReadWriteLock getReadWriteLock(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        return this.redissonClient.getReadWriteLock(Ulid.from(id).toString());
    }

    public RAtomicLong getAtomicLong(UUID id, String parameter) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        String key;
        if (ValueUtil.isAnyNullOrEmpty(parameter)) {
            key = Ulid.from(id).toString();
        } else {
            key = Ulid.from(id) + parameter;
        }

        return this.redissonClient.getAtomicLong(key);
    }

    public <T> RBucket<T> getBucket(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        return this.redissonClient.getBucket(Ulid.from(id).toString());
    }

    public <K, V> RMap<K, V> getMap(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        return this.redissonClient.getMap(Ulid.from(id).toString());
    }
}
