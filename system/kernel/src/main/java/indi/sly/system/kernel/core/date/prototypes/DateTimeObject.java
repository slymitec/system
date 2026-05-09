package indi.sly.system.kernel.core.date.prototypes;

import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.prototypes.ACacheableObject;
import indi.sly.system.kernel.core.values.NoneCacheEntity;
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
public class DateTimeObject extends ACacheableObject<NoneCacheEntity> {
    public long getCurrentDateTime() {
        KernelSpaceDefinition kernelSpace = this.coreManager.getKernelSpace();

        return Clock.systemUTC().instant().toEpochMilli() + kernelSpace.getSystemTimeOffset();
    }

    public void setDateTime(long dateTime) {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();

        if (!currentProcessToken.isPrivileges(PrivilegeType.CORE_MODIFY_DATETIME)) {
            throw new ConditionRefuseException();
        }

        KernelSpaceDefinition kernelSpace = this.coreManager.getKernelSpace();

        kernelSpace.setSystemTimeOffset(dateTime - Clock.systemUTC().instant().toEpochMilli());
    }
}
