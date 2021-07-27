package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AValueProcessPrototype;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessSessionObject extends AValueProcessPrototype<ProcessEntity> {
    protected ProcessObject process;

    private ProcessObject getParentProcessAndCheckIsCurrent() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject currentProcess = processManager.getCurrent();

        if (!currentProcess.getID().equals(process.getParentProcessID())) {
            throw new ConditionPermissionsException();
        }

        return currentProcess;
    }

    private ProcessSessionObject getParentProcessSessionAndCheckIsCurrent() {
        return this.getParentProcessAndCheckIsCurrent().getSession();
    }

    private ProcessTokenObject getParentProcessTokenAndCheckIsCurrent() {
        return this.getParentProcessAndCheckIsCurrent().getToken();
    }

    public UUID getID() {
        this.init();

        return this.value.getSessionID();
    }

    public void inheritID() {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.INITIALIZATION)
                || this.process.isCurrent()) {
            throw new StatusRelationshipErrorException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setSessionID(this.getParentProcessSessionAndCheckIsCurrent().getID());

        this.fresh();
        this.lock(LockType.NONE);
    }

    public void setID(UUID sessionID) {
        if (LogicalUtil.allNotEqual(this.process.getStatus().get(), ProcessStatusType.INITIALIZATION,
                ProcessStatusType.RUNNING)) {
            throw new StatusRelationshipErrorException();
        }

        ProcessTokenObject parentProcessToken = this.getParentProcessTokenAndCheckIsCurrent();

        if (!parentProcessToken.isPrivileges(PrivilegeType.SESSION_MODIFY_USER_SESSION)) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setSessionID(sessionID);

        this.fresh();
        this.lock(LockType.NONE);
    }
}
