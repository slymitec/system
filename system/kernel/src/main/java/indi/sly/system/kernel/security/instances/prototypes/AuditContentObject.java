package indi.sly.system.kernel.security.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.lang.StatusInsufficientResourcesException;
import indi.sly.system.common.supports.ArrayUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.instances.values.PortDefinition;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.security.instances.values.AuditDefinition;
import indi.sly.system.kernel.security.values.UserIDDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuditContentObject extends AInfoContentObject {
    @Override
    protected void read(byte[] source) {
        this.audit = ObjectUtil.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtil.transferToByteArray(this.audit);
    }

    private AuditDefinition audit;

    public UUID getProcessID() {
        this.init();

        return this.audit.getProcessID();
    }

    public void setProcessID(UUID processID) {
        try {
            this.lock(LockType.WRITE);
            this.init();

            this.audit.setProcessID(processID);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public UUID getAccountID() {
        this.init();

        return this.audit.getAccountID();
    }

    public void setAccountID(UUID accountID) {
        try {
            this.lock(LockType.WRITE);
            this.init();

            this.audit.setAccountID(accountID);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public Set<UserIDDefinition> getUserIDs() {
        this.init();

        return Collections.unmodifiableSet(this.audit.getUserIDs());
    }

    public void setUserIDs(Set<UserIDDefinition> userUDs) {
        if (ObjectUtil.isAnyNull(userUDs)) {
            throw new ConditionParametersException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.audit.getUserIDs().clear();
            this.audit.getUserIDs().addAll(userUDs);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public long getAudit() {
        this.init();

        return this.audit.getAudit();
    }

    public void setAudit(long audit) {
        try {
            this.lock(LockType.WRITE);
            this.init();

            this.audit.setAudit(audit);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
