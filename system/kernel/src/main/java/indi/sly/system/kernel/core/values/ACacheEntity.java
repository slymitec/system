package indi.sly.system.kernel.core.values;

import org.redisson.api.annotation.RId;

import java.util.UUID;

public abstract class ACacheEntity extends AEntity {
    @SuppressWarnings("NullAway.Init")
    @RId
    protected UUID id;
    @SuppressWarnings("NullAway.Init")
    protected Long duration;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getDuration() {
        return this.duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }
}
