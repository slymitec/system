package indi.sly.system.kernel.core.date.prototypes;

import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.time.Clock;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DateTimeObject extends AObject {
    public DateTimeObject() {
        this.clock = Clock.systemUTC();
        this.offset = 0;
    }

    private final Clock clock;
    private long offset;

    public long getCurrentDateTime() {
        return this.clock.instant().toEpochMilli() + this.offset;
    }

    public void setDateTime(long dateTime) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();

        if (!currentProcessToken.isPrivileges(PrivilegeType.CORE_MODIFY_DATETIME)) {
            throw new ConditionRefuseException();
        }

        this.offset = dateTime - this.clock.instant().toEpochMilli();
    }
}
