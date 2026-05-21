package indi.sly.system.kernel.security.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.prototypes.IByteValueSupporter;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.security.instances.values.AuditDefinition;
import indi.sly.system.kernel.security.values.UserIdDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuditContentObject extends AInfoContentObject implements IByteValueSupporter<AuditDefinition> {
    public UUID getProcessID() {
        AuditDefinition audit = this.init(this.read());

        return audit.getProcessId();
    }

    public UUID getAccountID() {
        AuditDefinition audit = this.init(this.read());

        return audit.getAccountId();
    }

    public PathDefinition getPath() {
        AuditDefinition audit = this.init(this.read());

        return audit.getPath();
    }

    public void setPath(PathDefinition path) {
        if (ObjectUtil.isAnyNull(path)) {
            throw new ConditionParametersException();
        }

        AuditDefinition audit = this.init(this.read());

        audit.setPath(path);

        this.flush(audit);
    }

    public Set<UserIdDefinition> getUserIds() {
        AuditDefinition audit = this.init(this.read());

        return CollectionUtil.unmodifiable(audit.getUserIds());
    }

    public void setUserIds(Set<UserIdDefinition> userIds) {
        if (ObjectUtil.isAnyNull(userIds)) {
            throw new ConditionParametersException();
        }

        AuditDefinition audit = this.init(this.read());

        audit.getUserIds().clear();
        audit.getUserIds().addAll(userIds);

        this.flush(audit);
    }

    public long getAudit() {
        AuditDefinition audit = this.init(this.read());

        return audit.getAudit();
    }

    public void setAudit(long value) {
        AuditDefinition audit = this.init(this.read());

        audit.setAudit(value);

        this.flush(audit);
    }
}
