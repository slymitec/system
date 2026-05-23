package indi.sly.system.kernel.core.values;

import org.redisson.api.annotation.REntity;

import java.util.Objects;

@REntity
public class NoneCacheEntity extends ACacheEntity {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
