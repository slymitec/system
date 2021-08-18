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
    public void setAccountAuthorization(AccountAuthorizationObject accountAuthorization) {
        if (ObjectUtil.isAnyNull(accountAuthorization)) {
            throw new ConditionParametersException();
        }

        if (this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        AccountAuthorizationResultDefinition accountAuthorizationResult = accountAuthorization.checkAndGetResult();

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setAccountID(accountAuthorizationResult.getID());
            AccountAuthorizationTokenDefinition accountAuthorizationResultToken = accountAuthorizationResult.getToken();
            this.value.setPrivileges(accountAuthorizationResultToken.getPrivileges());
            Map<Long, Integer> processTokenLimits = this.value.getLimits();
            processTokenLimits.clear();
            processTokenLimits.putAll(accountAuthorizationResultToken.getLimits());

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public UUID getAccountID() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        this.init();

        return this.value.getAccountID();
    }

    public void inheritAccountID() {
        if (this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();

        if (!process.getID().equals(parent.getParentID())) {
            throw new ConditionRefuseException();
        }

        ProcessTokenObject processToken = process.getToken();

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setAccountID(processToken.getAccountID());

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public long getPrivileges() {
        this.init();

        return this.value.getPrivileges();
    }

    public void inheritPrivileges() {
        if (this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();

        if (!process.getID().equals(parent.getParentID())) {
            throw new ConditionRefuseException();
        }

        ProcessTokenObject processToken = process.getToken();

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setPrivileges(processToken.getPrivileges());

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void inheritPrivileges(long privileges) {
        if (this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();

        if (!process.getID().equals(parent.getParentID())) {
            throw new ConditionRefuseException();
        }

        ProcessTokenObject processToken = process.getToken();

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setPrivileges(LogicalUtil.and(privileges, processToken.getPrivileges()));

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void setPrivileges(long privileges) {
        if (this.parent.isCurrent()) {
            if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING)) {
                throw new StatusRelationshipErrorException();
            } else {
                if (!this.isPrivileges(PrivilegeType.CORE_MODIFY_PRIVILEGES)) {
                    throw new ConditionRefuseException();
                }
            }
        } else {
            if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
                throw new StatusRelationshipErrorException();
            }

            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessTokenObject processToken = process.getToken();

            if (!process.getID().equals(parent.getParentID())) {
                throw new ConditionRefuseException();
            }
            if (!processToken.isPrivileges(PrivilegeType.CORE_MODIFY_PRIVILEGES)) {
                throw new ConditionRefuseException();
            }
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.value.setPrivileges(privileges);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public boolean isPrivileges(long privileges) {
        return LogicalUtil.isAllExist(this.getPrivileges(), privileges);
    }

    public Map<Long, Integer> getLimits() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        this.init();

        return CollectionUtil.unmodifiable(this.value.getLimits());
    }

    public void inheritLimits() {
        if (this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();

            if (!process.getID().equals(parent.getParentID())) {
                throw new ConditionRefuseException();
            }

            ProcessTokenObject processToken = process.getToken();

            Map<Long, Integer> processTokenLimits = this.getLimits();
            processTokenLimits.clear();
            processTokenLimits.putAll(processToken.getLimits());

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void setLimits(Map<Long, Integer> limits) {
        if (ObjectUtil.isAnyNull(limits)) {
            throw new ConditionParametersException();
        }

        if (this.parent.isCurrent()) {
            if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.RUNNING)) {
                throw new StatusRelationshipErrorException();
            } else {
                if (!this.isPrivileges(PrivilegeType.PROCESSES_MODIFY_LIMITS)) {
                    throw new ConditionRefuseException();
                }
            }
        } else {
            if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION)) {
                throw new StatusRelationshipErrorException();
            }

            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessTokenObject processToken = process.getToken();

            if (!process.getID().equals(parent.getParentID())) {
                throw new ConditionRefuseException();
            }
            if (!processToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_LIMITS)) {
                throw new ConditionRefuseException();
            }
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Map<Long, Integer> processTokenLimits = this.getLimits();
            processTokenLimits.clear();
            processTokenLimits.putAll(limits);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public Set<UUID> getRoles() {
        if (LogicalUtil.allNotEqual(this.parent.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING, ProcessStatusType.DIED)) {
            throw new StatusRelationshipErrorException();
        }

        this.init();

        return CollectionUtil.unmodifiable(this.value.getRoles());
    }

    public void setRoles(Set<UUID> roles) {
        if (ObjectUtil.isAnyNull(roles)) {
            throw new ConditionParametersException();
        }

        if (this.parent.isCurrent() || LogicalUtil.allNotEqual(this.parent.getStatus().get(),
                ProcessStatusType.INITIALIZATION)) {
            throw new StatusRelationshipErrorException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();

            if (!process.getID().equals(parent.getParentID())) {
                throw new ConditionRefuseException();
            }

            this.value.getRoles().addAll(roles);

        } finally {
            this.lock(LockType.NONE);
        }
    }
}
