package indi.sly.system.kernel.security.values;

import indi.sly.system.kernel.core.values.ACacheEntity;
import indi.sly.system.kernel.objects.values.InfoCacheEntity;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RObjectField;

import java.util.Objects;

@REntity
public class SecurityDescriptorCacheEntity extends ACacheEntity {
    @RObjectField
    private InfoCacheEntity info;
    private boolean permission;
    private boolean audit;

    public InfoCacheEntity getInfo() {
        return info;
    }

    public void setInfo(InfoCacheEntity info) {
        this.info = info;
    }

    public boolean isPermission() {
        return permission;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    public boolean isAudit() {
        return audit;
    }

    public void setAudit(boolean audit) {
        this.audit = audit;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SecurityDescriptorCacheEntity cache)) return false;
        return Objects.equals(id, cache.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
