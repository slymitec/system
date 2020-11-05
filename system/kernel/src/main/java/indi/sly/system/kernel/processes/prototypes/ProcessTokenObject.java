package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.LogicalUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.prototypes.ABytesProcessObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.security.SecurityTokenManager;
import indi.sly.system.kernel.security.prototypes.PrivilegeTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
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

    public UUID getAccountID() {
        this.init();

        return this.processToken.getAccountID();
    }

    public long getPrivilegeTypes() {
        this.init();

        return this.processToken.getPrivilegeTypes();
    }

    public long getRoleTypes() {
        this.init();

        return this.processToken.getRoleTypes();
    }

    private ProcessTokenDefinition getAndCheckProcessToken() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject currentProcess = processManager.getCurrentProcess();

        if (!currentProcess.getID().equals(process.getParentProcessID())) {
            throw new ConditionPermissionsException();
        }

        return currentProcess.getToken().processToken;
    }

    public void inheritAccountID() {
        this.lock(LockTypes.WRITE);
        this.init();

        this.processToken.setAccountID(this.getAndCheckProcessToken().getAccountID());

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public void inheritPrivilegeTypes() {
        this.lock(LockTypes.WRITE);
        this.init();

        this.processToken.setPrivilegeTypes(this.getAndCheckProcessToken().getPrivilegeTypes());

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

    public void setRoleTypes(long roleTypes) {
        this.lock(LockTypes.WRITE);
        this.init();

        this.processToken.setRoleTypes(roleTypes);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public boolean isPrivilegeTypes(long privilegeTypes) {
        return LogicalUtils.isAllExist(this.getPrivilegeTypes(), privilegeTypes);
    }

    public boolean isRoleTypes(long roleTypes) {
        return LogicalUtils.isAllExist(this.getRoleTypes(), roleTypes);
    }
}
