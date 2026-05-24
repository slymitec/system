package indi.sly.system.kernel.core.values;

import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.common.values.PathDefinition;
import org.redisson.api.annotation.RId;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.jsontype.TypeDeserializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class ACacheEntity extends AEntity {
    @RId
    private UUID id;
    //@JsonDeserialize(using = ForceLongDeserializer.class)
    private Long duration;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ACacheEntity cache = (ACacheEntity) o;
        return Objects.equals(id, cache.id) && Objects.equals(duration, cache.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, duration);
    }
}
