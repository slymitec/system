package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.values.LockTypes;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.ABytesValueProcessPrototype;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.values.ProcessTokenDefinition;
import indi.sly.system.kernel.processes.types.ProcessStatusTypes;
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
    private ProcessObject process;

    public void setProcess(ProcessObject process) {
        this.process = process;
    }

    private ProcessTokenObject getAndCheckParentProcessToken() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject currentProcess = processManager.getCurrentProcess();

        if (!currentProcess.getID().equals(process.getParentProcessID())) {
            throw new ConditionPermissionsException();
        }

        return currentProcess.getToken();
    }

    public void setAccountAuthorization(AccountAuthorizationObject accountAuthorization) {
        if (ObjectUtil.isAnyNull(accountAuthorization)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        AccountAuthorizationResultDefinition accountAuthorizationResult = accountAuthorization.checkAndGetResult();

        this.lock(LockTypes.WRITE);
        this.init();

        this.value.setAccountID(accountAuthorizationResult.getAccountID());
        AccountGroupTokenDefinition accountAuthorizationResultToken = accountAuthorizationResult.getToken();
        this.value.setPrivilegeTypes(accountAuthorizationResultToken.getPrivilegeTypes());
        Map<Long, Integer> processTokenLimits = this.value.getLimits();
        processTokenLimits.clear();
        processTokenLimits.putAll(accountAuthorizationResultToken.getLimits());

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public UUID getAccountID() {
        this.init();

        return this.value.getAccountID();
    }

    public void inheritAccountID() {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.value.setAccountID(this.getAndCheckParentProcessToken().getAccountID());

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getPrivilegeTypes() {
        this.init();

        return this.value.getPrivilegeTypes();
    }

    public void inheritPrivilegeTypes() {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.value.setPrivilegeTypes(this.getAndCheckParentProcessToken().getPrivilegeTypes());

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void inheritPrivilegeTypes(long privilegeTypes) {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.value.setPrivilegeTypes(LogicalUtil.and(privilegeTypes,
                this.getAndCheckParentProcessToken().getPrivilegeTypes()));

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void setPrivilegeTypes(long privilegeTypes) {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION,
                ProcessStatusTypes.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        if (this.process.isCurrent()) {
            if (!this.isPrivilegeTypes(PrivilegeTypes.CORE_MODIFY_PRIVILEGES)) {
                throw new ConditionPermissionsException();
            }
        } else {
            ProcessTokenObject parentProcessToken = this.getAndCheckParentProcessToken();

            if (!parentProcessToken.isPrivilegeTypes(PrivilegeTypes.CORE_MODIFY_PRIVILEGES)) {
                throw new ConditionPermissionsException();
            }
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.value.setPrivilegeTypes(privilegeTypes);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public Map<Long, Integer> getLimits() {
        this.init();

        return Collections.unmodifiableMap(this.value.getLimits());
    }

    public void inheritLimits() {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        Map<Long, Integer> processTokenLimits = this.getLimits();
        processTokenLimits.clear();
        processTokenLimits.putAll(this.getAndCheckParentProcessToken().getLimits());

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void setLimits(Map<Long, Integer> limits) {
        if (ObjectUtil.isAnyNull(limits)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION,
                ProcessStatusTypes.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        if (this.process.isCurrent()) {
            if (!this.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_LIMITS)) {
                throw new ConditionPermissionsException();
            }
        } else {
            ProcessTokenObject parentProcessToken = this.getAndCheckParentProcessToken();

            if (!parentProcessToken.isPrivilegeTypes(PrivilegeTypes.CORE_MODIFY_PRIVILEGES)) {
                throw new ConditionPermissionsException();
            }
        }

        this.lock(LockTypes.WRITE);
        this.init();

        Map<Long, Integer> processTokenLimits = this.getLimits();
        processTokenLimits.clear();
        processTokenLimits.putAll(limits);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public Set<UUID> getRoles() {
        this.init();

        return this.value.getRoles();
    }

    public void inheritRoleTypes() {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        Set<UUID> processTokenRoles = this.value.getRoles();
        processTokenRoles.clear();
        processTokenRoles.addAll(this.getAndCheckParentProcessToken().getRoles());

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void inheritRoleTypes(Set<UUID> roles) {
        if (ObjectUtil.isAnyNull(roles)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        Set<UUID> parentRoles = this.getAndCheckParentProcessToken().getRoles();
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
        this.lock(LockTypes.NONE);
    }

    public void setRoleTypes(Set<UUID> roles) {
        if (ObjectUtil.isAnyNull(roles)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION,
                ProcessStatusTypes.RUNNING) || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.getAndCheckParentProcessToken();

        this.lock(LockTypes.WRITE);
        this.init();

        Set<UUID> processTokenRoles = this.value.getRoles();
        processTokenRoles.clear();
        processTokenRoles.addAll(roles);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public boolean isPrivilegeTypes(long privilegeTypes) {
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
