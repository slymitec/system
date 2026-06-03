package indi.sly.subsystem.periphery.memory.repositories.prototypes;

import indi.sly.subsystem.periphery.core.prototypes.AObject;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;

import jakarta.annotation.Resource;
import jakarta.inject.Named;
import org.redisson.api.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DistributionRepositoryObject extends AObject {
    @Resource
    private RedissonClient redissonClient;

    private String acquireKey(String prefix, UUID id, String parameter) {
        if (ValueUtil.isAnyNullOrEmpty(prefix, id)) {
            throw new ConditionParametersException();
        }

        if (ValueUtil.isAnyNullOrEmpty(parameter)) {
            return prefix + "_" + UUIDUtil.toString(id);
        } else {
            return prefix + "_" + UUIDUtil.toString(id) + "_" + parameter;
        }
    }

    public RLock getLock(String prefix, UUID id, String parameter) {
        String key = this.acquireKey(prefix, id, parameter);

        return this.redissonClient.getLock(key);
    }

    public RReadWriteLock getReadWriteLock(String prefix, UUID id, String parameter) {
        String key = this.acquireKey(prefix, id, parameter);

        return this.redissonClient.getReadWriteLock(key);
    }

    public RAtomicLong getAtomicLong(String prefix, UUID id, String parameter) {
        String key = this.acquireKey(prefix, id, parameter);

        return this.redissonClient.getAtomicLong(key);
    }

    public <T> RBucket<T> getBucket(String prefix, UUID id, String parameter) {
        String key = this.acquireKey(prefix, id, parameter);

        return this.redissonClient.getBucket(key);
    }

    public <V> RSet<V> getSet(String prefix, UUID id, String parameter) {
        String key = this.acquireKey(prefix, id, parameter);

        return this.redissonClient.getSet(key);
    }

    public <K, V> RMap<K, V> getMap(String prefix, UUID id, String parameter) {
        String key = this.acquireKey(prefix, id, parameter);

        return this.redissonClient.getMap(key);
    }
}
