package indi.sly.system.kernel.security.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.prototypes.IByteValueProcess;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.security.instances.values.AuditDefinition;
import indi.sly.system.kernel.security.values.UserIDDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuditContentObject extends AInfoContentObject implements IByteValueProcess<AuditDefinition> {
    public UUID getProcessID() {
        AuditDefinition audit = this.init(this.read());

        return audit.getProcessID();
    }

    public UUID getAccountID() {
        AuditDefinition audit = this.init(this.read());

        return audit.getAccountID();
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

    public Set<UserIDDefinition> getUserIDs() {
        AuditDefinition audit = this.init(this.read());

        return CollectionUtil.unmodifiable(audit.getUserIDs());
    }

    public void setUserIDs(Set<UserIDDefinition> userUDs) {
        if (ObjectUtil.isAnyNull(userUDs)) {
            throw new ConditionParametersException();
        }

        AuditDefinition audit = this.init(this.read());

        audit.getUserIDs().clear();
        audit.getUserIDs().addAll(userUDs);

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
