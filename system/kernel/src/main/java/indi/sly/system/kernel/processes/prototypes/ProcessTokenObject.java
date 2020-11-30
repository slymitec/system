package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.common.exceptions.StatusRelationshipErrorException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.LogicalUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.prototypes.ABytesProcessObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.definitions.ProcessTokenDefinition;
import indi.sly.system.kernel.processes.types.ProcessStatusTypes;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.types.PrivilegeTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessTokenObject extends ABytesProcessObject {
    @Override
    protected void read(byte[] source) {
        this.processToken = ObjectUtils.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtils.transferToByteArray(this.processToken);
    }

    private ProcessObject process;
    private ProcessTokenDefinition processToken;

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

    public UUID getAccountID() {
        this.init();

        return this.processToken.getAccountID();
    }

    public void inheritAccountID() {
        if (LogicalUtils.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processToken.setAccountID(this.getAndCheckParentProcessToken().getAccountID());

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void setAccountID(AccountAuthorizationObject accountAuthorization) {
        if (ObjectUtils.isAnyNull(accountAuthorization)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtils.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        UUID accountID = accountAuthorization.checkAndGetAccountID();

        this.lock(LockTypes.WRITE);
        this.init();

        this.processToken.setAccountID(accountID);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public long getPrivilegeTypes() {
        this.init();

        return this.processToken.getPrivilegeTypes();
    }

    public void inheritPrivilegeTypes() {
        if (LogicalUtils.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processToken.setPrivilegeTypes(this.getAndCheckParentProcessToken().getPrivilegeTypes());

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void inheritPrivilegeTypes(long privilegeTypes) {
        if (LogicalUtils.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processToken.setPrivilegeTypes(LogicalUtils.and(privilegeTypes,
                this.getAndCheckParentProcessToken().getPrivilegeTypes()));

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void setPrivilegeTypes(long privilegeTypes) {
        if (LogicalUtils.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION,
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

        this.processToken.setPrivilegeTypes(privilegeTypes);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public Map<Long, Integer> getLimits() {
        this.init();

        return Collections.unmodifiableMap(this.processToken.getLimits());
    }

    public void inheritLimits() {
        if (LogicalUtils.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION)
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
        if (ObjectUtils.isAnyNull(limits)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtils.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION,
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

        return this.processToken.getRoles();
    }

    public void inheritRoleTypes() {
        if (LogicalUtils.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        Set<UUID> processTokenRoles = this.processToken.getRoles();
        processTokenRoles.clear();
        processTokenRoles.addAll(this.getAndCheckParentProcessToken().getRoles());

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void inheritRoleTypes(Set<UUID> roles) {
        if (ObjectUtils.isAnyNull(roles)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtils.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION)
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

        Set<UUID> processTokenRoles = this.processToken.getRoles();
        processTokenRoles.clear();
        processTokenRoles.addAll(childRoles);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void setRoleTypes(Set<UUID> roles) {
        if (ObjectUtils.isAnyNull(roles)) {
            throw new ConditionParametersException();
        }

        if (LogicalUtils.allNotEqual(this.process.getStatus().get(), ProcessStatusTypes.INITIALIZATION,
                ProcessStatusTypes.RUNNING) || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.getAndCheckParentProcessToken();

        this.lock(LockTypes.WRITE);
        this.init();

        Set<UUID> processTokenRoles = this.processToken.getRoles();
        processTokenRoles.clear();
        processTokenRoles.addAll(roles);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public boolean isPrivilegeTypes(long privilegeTypes) {
        this.init();

        return LogicalUtils.isAllExist(this.getPrivilegeTypes(), privilegeTypes);
    }

    public boolean isRoleTypes(UUID roleType) {
        if (ObjectUtils.isAnyNull(roleType)) {
            throw new ConditionParametersException();
        }

        this.init();

        return this.getRoles().contains(roleType);
    }
}
