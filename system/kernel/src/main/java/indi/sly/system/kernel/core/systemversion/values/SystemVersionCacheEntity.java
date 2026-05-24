package indi.sly.system.kernel.core.systemversion.values;

import indi.sly.system.kernel.core.values.ACacheEntity;
import org.redisson.api.annotation.REntity;

import java.util.Objects;

@REntity
public class SystemVersionCacheEntity extends ACacheEntity {
    private String systemVersion;

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SystemVersionCacheEntity cache)) return false;
        return Objects.equals(id, cache.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
