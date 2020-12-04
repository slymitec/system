package indi.sly.system.kernel.core.date.prototypes;

import java.time.Clock;

import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.kernel.core.prototypes.ACorePrototype;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.types.PrivilegeTypes;

public class DateTimeObject extends ACorePrototype {
    public DateTimeObject() {
        this.clock = Clock.systemUTC();
        this.offset = 0;
    }

    private Clock clock;
    private long offset;

    public long getCurrentDateTime() {
        return this.clock.instant().toEpochMilli() + this.offset;
    }

    public void setDateTime(long dateTime) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrentProcess();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();

        if (!currentProcessToken.isPrivilegeTypes(PrivilegeTypes.CORE_MODIFY_DATETIME)) {
            throw new ConditionPermissionsException();
        }

        this.offset = dateTime - this.clock.instant().toEpochMilli();
    }
}
