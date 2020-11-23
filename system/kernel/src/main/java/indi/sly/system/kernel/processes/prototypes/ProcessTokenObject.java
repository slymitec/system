package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.common.exceptions.StatusRelationshipErrorException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.LogicalUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.prototypes.ABytesProcessObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.security.prototypes.PrivilegeTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    private ProcessTokenDefinition getAndCheckParentProcessToken() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject currentProcess = processManager.getCurrentProcess();

        if (!currentProcess.getID().equals(process.getParentProcessID())) {
            throw new ConditionPermissionsException();
        }

        return currentProcess.getToken().processToken;
    }

    public UUID getAccountID() {
        this.init();

        return this.processToken.getAccountID();
    }

    public void inheritAccountID() {
        if (this.process.getStatus().get() != ProcessStatusTypes.INITIALIZATION) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processToken.setAccountID(this.getAndCheckParentProcessToken().getAccountID());

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void setAccountID(UUID accountID) {
        if (!this.isPrivilegeTypes(PrivilegeTypes.PROCESSES_RUN_APP_WITH_ANOTHER_ACCOUNT)) {
            throw new ConditionPermissionsException();
        }

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
        if (this.process.getStatus().get() != ProcessStatusTypes.INITIALIZATION) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processToken.setPrivilegeTypes(this.getAndCheckParentProcessToken().getPrivilegeTypes());

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void inheritPrivilegeTypes(long privilegeTypes) {
        if (this.process.getStatus().get() != ProcessStatusTypes.INITIALIZATION) {
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
        if (!this.isPrivilegeTypes(PrivilegeTypes.CORE_MODIFY_PRIVILEGES)) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processToken.setPrivilegeTypes(privilegeTypes);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public Set<UUID> getRoles() {
        this.init();

        return processToken.getRoles();
    }

    public void inheritRoleTypes() {
        if (this.process.getStatus().get() != ProcessStatusTypes.INITIALIZATION) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processToken.setRoles(new HashSet<>(this.getAndCheckParentProcessToken().getRoles()));

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void inheritRoleTypes(Set<UUID> roles) {
        if (ObjectUtils.isAnyNull(roles)) {
            throw new ConditionParametersException();
        }

        if (this.process.getStatus().get() != ProcessStatusTypes.INITIALIZATION) {
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

        this.processToken.setRoles(childRoles);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void setRoleTypes(Set<UUID> roles) {
        if (ObjectUtils.isAnyNull(roles)) {
            throw new ConditionParametersException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.processToken.setRoles(roles);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public boolean isPrivilegeTypes(long privilegeTypes) {
        return LogicalUtils.isAllExist(this.getPrivilegeTypes(), privilegeTypes);
    }

    public boolean isRoleTypes(UUID roleType) {
        if (ObjectUtils.isAnyNull(roleType)) {
            throw new ConditionParametersException();
        }

        return this.getRoles().contains(roleType);
    }
}
