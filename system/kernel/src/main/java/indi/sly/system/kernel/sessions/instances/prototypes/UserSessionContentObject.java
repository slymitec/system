package indi.sly.system.kernel.sessions.instances.prototypes;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.common.exceptions.StatusAlreadyExistedException;
import indi.sly.system.common.exceptions.StatusNotExistedException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.types.PrivilegeTypes;
import indi.sly.system.kernel.sessions.instances.definitions.ClientDefinition;
import indi.sly.system.kernel.sessions.instances.definitions.UserSessionDefinition;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UserSessionContentObject extends AInfoContentObject {
    @Override
    protected void read(byte[] source) {
        this.userSession = ObjectUtils.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtils.transferToByteArray(this.userSession);
    }

    private UserSessionDefinition userSession;

    private void checkProcessTokenPrivilege() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivilegeTypes(PrivilegeTypes.SESSION_MODIFY_USERSESSION)) {
            throw new ConditionPermissionsException();
        }
    }

    private void checkProcessTokenAccountID() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.getAccountID().equals(this.getAccountID())) {
            throw new ConditionPermissionsException();
        }
    }

    public long getType() {
        this.init();

        return this.userSession.getType();
    }

    public void setType(long type) {
        this.checkProcessTokenPrivilege();

        this.lock(LockTypes.WRITE);
        this.init();

        this.userSession.setType(type);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public UUID getAccountID() {
        this.init();

        return this.userSession.getAccountID();
    }

    public void setAccountID(UUID accountID) {
        this.checkProcessTokenPrivilege();

        this.lock(LockTypes.WRITE);
        this.init();

        this.userSession.setAccountID(accountID);

        this.fresh();
        this.lock(LockTypes.NONE);
    }

    public Set<UUID> getProcessIDs() {
        this.init();

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.getAccountID().equals(this.userSession.getAccountID())) {
            throw new ConditionPermissionsException();
        }

        return Collections.unmodifiableSet(this.userSession.getProcessIDs());
    }

    public void addProcessID(UUID processID) {
        this.checkProcessTokenAccountID();

        this.lock(LockTypes.WRITE);
        this.init();

        boolean result = this.userSession.getProcessIDs().add(processID);

        this.fresh();
        this.lock(LockTypes.NONE);

        if (!result) {
            throw new StatusAlreadyExistedException();
        }
    }

    public void deleteProcessID(UUID processID) {
        this.checkProcessTokenAccountID();

        this.lock(LockTypes.WRITE);
        this.init();

        boolean result = this.userSession.getProcessIDs().remove(processID);

        this.fresh();
        this.lock(LockTypes.NONE);

        if (!result) {
            throw new StatusNotExistedException();
        }
    }

    public ClientDefinition getClient() {
        this.init();

        return this.userSession.getClient();
    }

    public void setClient(ClientDefinition client) {
        if (ObjectUtils.isAnyNull(client)) {
            throw new ConditionParametersException();
        }

        this.checkProcessTokenPrivilege();

        this.lock(LockTypes.WRITE);
        this.init();

        this.userSession.setClient(client);

        this.fresh();
        this.lock(LockTypes.NONE);
    }
}
