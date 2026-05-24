package indi.sly.system.test;

import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;
import java.util.UUID;

@REntity
public class TestEntity {
    @RId
    private UUID id;
    @JsonDeserialize(using = TestLongDeserialize.class)
    private Long n;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getN() {
        return n;
    }

    public void setN(Long n) {
        this.n = n;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TestEntity that = (TestEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(n, that.n);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, n);
    }

    public static class TestLongDeserialize extends ValueDeserializer<Long> {
        @Override
        public Long deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            return 321L;
        }
    }
}