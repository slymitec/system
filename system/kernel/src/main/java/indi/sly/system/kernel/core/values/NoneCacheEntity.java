package indi.sly.system.kernel.core.values;

import org.redisson.api.annotation.REntity;

import java.util.Objects;

@REntity
public class NoneCacheEntity extends ACacheEntity {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NoneCacheEntity cache)) return false;
        return Objects.equals(id, cache.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
