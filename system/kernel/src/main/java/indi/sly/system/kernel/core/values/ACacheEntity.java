package indi.sly.system.kernel.core.values;

import org.redisson.api.annotation.RId;

import java.util.Objects;
import java.util.UUID;

public abstract class ACacheEntity extends AEntity {
    @RId
    private UUID id;
    private long duration;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ACacheEntity cache = (ACacheEntity) o;
        return duration == cache.duration && Objects.equals(id, cache.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, duration);
    }
}
