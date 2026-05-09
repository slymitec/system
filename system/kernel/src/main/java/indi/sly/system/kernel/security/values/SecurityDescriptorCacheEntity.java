package indi.sly.system.kernel.security.values;

import com.redis.om.spring.annotations.Document;
import indi.sly.system.common.values.ADefinition;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.values.ACacheEntity;
import indi.sly.system.kernel.objects.values.InfoCacheEntity;
import org.springframework.data.annotation.Reference;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Document("SecurityDescriptorObject")
public class SecurityDescriptorCacheEntity extends ACacheEntity {
    @Reference
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
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SecurityDescriptorCacheEntity that = (SecurityDescriptorCacheEntity) o;
        return permission == that.permission && audit == that.audit && Objects.equals(info, that.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), info, permission, audit);
    }
}
