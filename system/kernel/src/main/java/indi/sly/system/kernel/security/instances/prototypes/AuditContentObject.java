package indi.sly.system.kernel.security.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.security.instances.values.AuditDefinition;
import indi.sly.system.kernel.security.values.UserIDDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuditContentObject extends AInfoContentObject {
    public AuditContentObject() {
        this.funcCustomRead = () -> this.audit = ObjectUtil.transferFromByteArray(this.value);
        this.funcCustomWrite = () -> this.value = ObjectUtil.transferToByteArray(this.audit);
    }

    private AuditDefinition audit;

    public UUID getProcessID() {
        try {
            this.lock(LockType.WRITE);
            this.init();

            return this.audit.getProcessID();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public UUID getAccountID() {
        try {
            this.lock(LockType.WRITE);
            this.init();

            return this.audit.getAccountID();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public List<IdentificationDefinition> getIdentifications() {
        try {
            this.lock(LockType.WRITE);
            this.init();

            return CollectionUtil.unmodifiable(this.audit.getIdentifications());
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void setIdentifications(List<IdentificationDefinition> identifications) {
        if (ObjectUtil.isAnyNull(identifications)) {
            throw new ConditionParametersException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.audit.getIdentifications().clear();
            this.audit.getIdentifications().addAll(identifications);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public Set<UserIDDefinition> getUserIDs() {
        try {
            this.lock(LockType.WRITE);
            this.init();

            return CollectionUtil.unmodifiable(this.audit.getUserIDs());
        } finally {
            this.lock(LockType.NONE);
        }
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
        try {
            this.lock(LockType.WRITE);
            this.init();

            return this.audit.getAudit();
        } finally {
            this.lock(LockType.NONE);
        }
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
