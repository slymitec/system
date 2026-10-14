package indi.sly.system.kernel.security.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.security.instances.values.AuditDefinition;
import indi.sly.system.kernel.security.values.UserIdRecord;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuditContentObject extends AInfoContentObject {
    public UUID getProcessId() {
        AuditDefinition audit = ObjectUtil.transferFromJsonByteArray(AuditDefinition.class, this.read());

        if (ObjectUtil.isAnyNull(audit)) {
            throw new StatusUnreadableException();
        }

        return audit.getProcessId();
    }

    public UUID getAccountId() {
        AuditDefinition audit = ObjectUtil.transferFromJsonByteArray(AuditDefinition.class, this.read());

        if (ObjectUtil.isAnyNull(audit)) {
            throw new StatusUnreadableException();
        }

        return audit.getAccountId();
    }

    public PathRecord getPath() {
        AuditDefinition audit = ObjectUtil.transferFromJsonByteArray(AuditDefinition.class, this.read());

        if (ObjectUtil.isAnyNull(audit)) {
            throw new StatusUnreadableException();
        }

        return audit.getPath();
    }

    public void setPath(PathRecord path) {
        if (ObjectUtil.isAnyNull(path)) {
            throw new ConditionParametersException();
        }

        AuditDefinition audit = ObjectUtil.transferFromJsonByteArray(AuditDefinition.class, this.read());

        if (ObjectUtil.isAnyNull(audit)) {
            throw new StatusUnreadableException();
        }

        audit.setPath(path);

        this.write(ObjectUtil.transferToJsonByteArray(audit));
    }

    public Set<UserIdRecord> getUserIds() {
        AuditDefinition audit = ObjectUtil.transferFromJsonByteArray(AuditDefinition.class, this.read());

        if (ObjectUtil.isAnyNull(audit)) {
            throw new StatusUnreadableException();
        }

        return CollectionUtil.unmodifiable(audit.getUserIds());
    }

    public void setUserIds(Set<UserIdRecord> userIds) {
        if (ObjectUtil.isAnyNull(userIds)) {
            throw new ConditionParametersException();
        }

        AuditDefinition audit = ObjectUtil.transferFromJsonByteArray(AuditDefinition.class, this.read());

        if (ObjectUtil.isAnyNull(audit)) {
            throw new StatusUnreadableException();
        }

        audit.getUserIds().clear();
        audit.getUserIds().addAll(userIds);

        this.write(ObjectUtil.transferToJsonByteArray(audit));
    }

    public long getAudit() {
        AuditDefinition audit = ObjectUtil.transferFromJsonByteArray(AuditDefinition.class, this.read());

        if (ObjectUtil.isAnyNull(audit)) {
            throw new StatusUnreadableException();
        }

        return audit.getAudit();
    }

    public void setAudit(long value) {
        AuditDefinition audit = ObjectUtil.transferFromJsonByteArray(AuditDefinition.class, this.read());

        if (ObjectUtil.isAnyNull(audit)) {
            throw new StatusUnreadableException();
        }

        audit.setAudit(value);

        this.write(ObjectUtil.transferToJsonByteArray(audit));
    }
}
