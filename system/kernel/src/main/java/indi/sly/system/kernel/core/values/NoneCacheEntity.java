package indi.sly.system.kernel.core.values;

import com.redis.om.spring.annotations.Document;

import java.util.Objects;

@Document("NoneObject")
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
