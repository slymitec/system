package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessPrototype;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.values.ProcessTokenDefinition;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
import indi.sly.system.kernel.security.values.AccountAuthorizationResultDefinition;
import indi.sly.system.kernel.security.values.AccountGroupTokenDefinition;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.types.PrivilegeTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessTokenObject extends ABytesValueProcessPrototype<ProcessTokenDefinition> {
    protected ProcessObject process;

    private ProcessObject getParentProcessAndCheckIsCurrent() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject currentProcess = processManager.getCurrentProcess();

        if (!currentProcess.getID().equals(process.getParentProcessID())) {
            throw new ConditionPermissionsException();
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

        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        AccountAuthorizationResultDefinition accountAuthorizationResult = accountAuthorization.checkAndGetResult();

        this.lock(LockType.WRITE);
        this.init();

        this.value.setAccountID(accountAuthorizationResult.getAccountID());
        AccountGroupTokenDefinition accountAuthorizationResultToken = accountAuthorizationResult.getToken();
        this.value.setPrivilegeTypes(accountAuthorizationResultToken.getPrivilegeTypes());
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
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setAccountID(this.getParentProcessTokenAndCheckIsCurrent().getAccountID());

        this.fresh();
        this.lock(LockType.NONE);
    }

    public long getPrivilegeTypes() {
        this.init();

        return this.value.getPrivilegeTypes();
    }

    public void inheritPrivilegeTypes() {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setPrivilegeTypes(this.getParentProcessTokenAndCheckIsCurrent().getPrivilegeTypes());

        this.fresh();
        this.lock(LockType.NONE);
    }

    public void inheritPrivilegeTypes(long privilegeTypes) {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setPrivilegeTypes(LogicalUtil.and(privilegeTypes,
                this.getParentProcessTokenAndCheckIsCurrent().getPrivilegeTypes()));

        this.fresh();
        this.lock(LockType.NONE);
    }

    public void setPrivilegeTypes(long privilegeTypes) {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        if (this.process.isCurrent()) {
            if (!this.isPrivilegeType(PrivilegeTypes.CORE_MODIFY_PRIVILEGES)) {
                throw new ConditionPermissionsException();
            }
        } else {
            ProcessTokenObject parentProcessToken = this.getParentProcessTokenAndCheckIsCurrent();

            if (!parentProcessToken.isPrivilegeType(PrivilegeTypes.CORE_MODIFY_PRIVILEGES)) {
                throw new ConditionPermissionsException();
            }
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setPrivilegeTypes(privilegeTypes);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public Map<Long, Integer> getLimits() {
        this.init();

        return Collections.unmodifiableMap(this.value.getLimits());
    }

    public void inheritLimits() {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.INITIALIZATION)
                || this.process.isCurrent()) {
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

        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        if (this.process.isCurrent()) {
            if (!this.isPrivilegeType(PrivilegeTypes.PROCESSES_MODIFY_LIMITS)) {
                throw new ConditionPermissionsException();
            }
        } else {
            ProcessTokenObject parentProcessToken = this.getParentProcessTokenAndCheckIsCurrent();

            if (!parentProcessToken.isPrivilegeType(PrivilegeTypes.CORE_MODIFY_PRIVILEGES)) {
                throw new ConditionPermissionsException();
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

        return this.value.getRoles();
    }

    public void inheritRoleTypes() {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockType.WRITE);
        this.init();

        Set<UUID> processTokenRoles = this.value.getRoles();
        processTokenRoles.clear();
        processTokenRoles.addAll(this.getParentProcessTokenAndCheckIsCurrent().getRoles());

        this.fresh();
        this.lock(LockType.NONE);
    }

    public void inheritRoleTypes(Set<UUID> roles) {
        if (ObjectUtil.isAnyNull(roles)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockType.WRITE);
        this.init();

        Set<UUID> parentRoles = this.getParentProcessTokenAndCheckIsCurrent().getRoles();
        Set<UUID> childRoles = new HashSet<>();

        for (UUID role : roles) {
            if (parentRoles.contains(role)) {
                childRoles.add(role);
            }
        }

        Set<UUID> processTokenRoles = this.value.getRoles();
        processTokenRoles.clear();
        processTokenRoles.addAll(childRoles);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public void setRoleTypes(Set<UUID> roles) {
        if (ObjectUtil.isAnyNull(roles)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING) || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.getParentProcessTokenAndCheckIsCurrent();

        this.lock(LockType.WRITE);
        this.init();

        Set<UUID> processTokenRoles = this.value.getRoles();
        processTokenRoles.clear();
        processTokenRoles.addAll(roles);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public boolean isPrivilegeType(long privilegeTypes) {
        this.init();

        return LogicalUtil.isAllExist(this.getPrivilegeTypes(), privilegeTypes);
    }

    public boolean isRoleTypes(UUID roleType) {
        if (ObjectUtil.isAnyNull(roleType)) {
            throw new ConditionParametersException();
        }

        this.init();

        return this.getRoles().contains(roleType);
    }
}
