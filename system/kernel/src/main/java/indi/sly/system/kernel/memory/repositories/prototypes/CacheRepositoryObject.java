package indi.sly.system.kernel.memory.repositories.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.*;
import indi.sly.system.kernel.core.prototypes.AObject;
import jakarta.annotation.Resource;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CacheRepositoryObject extends AObject {
    @Resource
    private RedisTemplate<String, byte[]> redisTemplate;

    private String convertKey(UUID id) {
        return "Memory:CacheRepository:" + id.toString();
    }

    public boolean contain(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        return this.redisTemplate.hasKey(this.convertKey(id));
    }

    public byte[] get(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        return this.redisTemplate.opsForValue().get(this.convertKey(id));
    }

    public byte[] getAndExpire(UUID id, Duration duration) {
        if (ValueUtil.isAnyNullOrEmpty(id) || ObjectUtil.isAnyNull(duration)) {
            throw new ConditionParametersException();
        }

        return this.redisTemplate.opsForValue().getAndExpire(this.convertKey(id), duration);
    }

    public void add(UUID id, byte[] value) {
        this.add(id, value, null);
    }

    public void add(UUID id, byte[] value, Duration duration) {
        if (ValueUtil.isAnyNullOrEmpty(id, value)) {
            throw new ConditionParametersException();
        }

        if (ObjectUtil.isAnyNull(duration)) {
            this.redisTemplate.opsForValue().set(this.convertKey(id), value);
        } else {
            this.redisTemplate.opsForValue().set(this.convertKey(id), value, duration);
        }
    }

    public void delete(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        this.redisTemplate.delete(this.convertKey(id));
    }

    public void expire(UUID id, Duration duration) {
        if (ValueUtil.isAnyNullOrEmpty(id) || ObjectUtil.isAnyNull(duration)) {
            throw new ConditionParametersException();
        }

        this.redisTemplate.expire(this.convertKey(id), duration);
    }
}
