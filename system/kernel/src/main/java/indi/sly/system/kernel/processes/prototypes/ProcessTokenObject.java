package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
import indi.sly.system.kernel.processes.values.ProcessTokenDefinition;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.values.AccountAuthorizationResultDefinition;
import indi.sly.system.kernel.security.values.AccountAuthorizationTokenDefinition;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessTokenObject extends ABytesValueProcessObject<ProcessTokenDefinition, ProcessObject> {
    private ProcessObject getParentProcessAndCheckIsCurrent() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject currentProcess = processManager.getCurrent();

        if (!currentProcess.getID().equals(parent.getParentID())) {
            throw new ConditionRefuseException();
        }

        return currentProcess;
    }

    private ProcessTokenObject getParentProcessTokenAndCheckIsCurrent() {
        return this.getParentProcessAndCheckIsCurrent().getToken();
    }

    public void setAccountAuthorization(AccountAuthorizationObject accountAuthorization) {
        if (ObjectUtil.isAnyNull(accountAuthorization)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION)
                || this.parent.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        AccountAuthorizationResultDefinition accountAuthorizationResult = accountAuthorization.checkAndGetResult();

        this.lock(LockType.WRITE);
        this.init();

        this.value.setAccountID(accountAuthorizationResult.getID());
        AccountAuthorizationTokenDefinition accountAuthorizationResultToken = accountAuthorizationResult.getToken();
        this.value.setPrivileges(accountAuthorizationResultToken.getPrivileges());
        Map<Long, Integer> processTokenLimits = this.value.getLimits();
        processTokenLimits.clear();
        processTokenLimits.putAll(accountAuthorizationResultToken.getLimits());

        this.fresh();
        this.lock(LockType.NONE);
    }

    public UUID getAccountID() {
        this.init();

        return this.value.getAccountID();
    }

    public void inheritAccountID() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION)
                || this.parent.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setAccountID(this.getParentProcessTokenAndCheckIsCurrent().getAccountID());

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getPrivileges() {
        this.init();

        return this.value.getPrivileges();
    }

    public void inheritPrivileges() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION)
                || this.parent.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setPrivileges(this.getParentProcessTokenAndCheckIsCurrent().getPrivileges());

        this.fresh();
        this.lock(LockType.NONE);
    }

    public void inheritPrivileges(long privileges) {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION)
                || this.parent.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setPrivileges(LogicalUtil.and(privileges,
                this.getParentProcessTokenAndCheckIsCurrent().getPrivileges()));

        this.fresh();
        this.lock(LockType.NONE);
    }

    public void setPrivileges(long privileges) {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        if (this.parent.isCurrent()) {
            if (!this.isPrivileges(PrivilegeType.CORE_MODIFY_PRIVILEGES)) {
                throw new ConditionRefuseException();
            }
        } else {
            ProcessTokenObject parentProcessToken = this.getParentProcessTokenAndCheckIsCurrent();

            if (!parentProcessToken.isPrivileges(PrivilegeType.CORE_MODIFY_PRIVILEGES)) {
                throw new ConditionRefuseException();
            }
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setPrivileges(privileges);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public boolean isPrivileges(long privileges) {
        return LogicalUtil.isAllExist(this.getPrivileges(), privileges);
    }

    public Map<Long, Integer> getLimits() {
        this.init();

        return CollectionUtil.unmodifiable(this.value.getLimits());
    }

    public void inheritLimits() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION)
                || this.parent.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockType.WRITE);
        this.init();

        Map<Long, Integer> processTokenLimits = this.getLimits();
        processTokenLimits.clear();
        processTokenLimits.putAll(this.getParentProcessTokenAndCheckIsCurrent().getLimits());

        this.fresh();
        this.lock(LockType.NONE);
    }

    public void setLimits(Map<Long, Integer> limits) {
        if (ObjectUtil.isAnyNull(limits)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        if (this.parent.isCurrent()) {
            if (!this.isPrivileges(PrivilegeType.PROCESSES_MODIFY_LIMITS)) {
                throw new ConditionRefuseException();
            }
        } else {
            ProcessTokenObject parentProcessToken = this.getParentProcessTokenAndCheckIsCurrent();

            if (!parentProcessToken.isPrivileges(PrivilegeType.CORE_MODIFY_PRIVILEGES)) {
                throw new ConditionRefuseException();
            }
        }

        this.lock(LockType.WRITE);
        this.init();

        Map<Long, Integer> processTokenLimits = this.getLimits();
        processTokenLimits.clear();
        processTokenLimits.putAll(limits);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public Set<UUID> getRoles() {
        this.init();

        return CollectionUtil.unmodifiable(this.value.getRoles());
    }

    public void setRoles(Set<UUID> roles) {
        if (ObjectUtil.isAnyNull(roles)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION)
                || this.parent.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.getRoles().addAll(roles);

        this.fresh();
        this.lock(LockType.NONE);
    }
}
