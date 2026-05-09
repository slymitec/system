package indi.sly.system.kernel.core.systemversion.values;

import com.redis.om.spring.annotations.Document;
import indi.sly.system.kernel.core.values.ACacheEntity;

import java.util.Objects;

@Document("SystemVersionObject")
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
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SystemVersionCacheEntity that = (SystemVersionCacheEntity) o;
        return Objects.equals(systemVersion, that.systemVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), systemVersion);
    }
}
