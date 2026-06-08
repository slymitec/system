package indi.sly.system.kernel.core.date.prototypes;

import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.prototypes.ACacheableObject;
import indi.sly.system.kernel.core.values.NoneCacheEntity;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.DistributionRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.redisson.api.RBucket;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.time.Clock;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DateTimeObject extends ACacheableObject<NoneCacheEntity> {
    public long getCurrent() {
        KernelSpaceDefinition kernelSpace = this.coreManager.getKernelSpace();
        KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        DistributionRepositoryObject distributionRepository = memoryManager.getDistributionRepository();

        RBucket<Long> systemTimeOffset = distributionRepository.getBucket("DateTime", kernelConfiguration.CORE_PROTOTYPE_DATETIME_SYSTEM_TIME_OFFSET, null);

        if (systemTimeOffset.isExists()) {
            return Clock.systemUTC().instant().toEpochMilli() + systemTimeOffset.get();
        } else {
            return Clock.systemUTC().instant().toEpochMilli();
        }
    }

    public void correct(long dateTime) {
        KernelSpaceDefinition kernelSpace = this.coreManager.getKernelSpace();
        KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject currentProcess = processManager.getCurrent();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();

        if (!currentProcessToken.isPrivileges(PrivilegeType.CORE_MODIFY_DATETIME)) {
            throw new ConditionRefuseException();
        }

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        DistributionRepositoryObject distributionRepository = memoryManager.getDistributionRepository();

        RBucket<Long> systemTimeOffset = distributionRepository.getBucket("DateTime", kernelConfiguration.CORE_PROTOTYPE_DATETIME_SYSTEM_TIME_OFFSET, null);

        systemTimeOffset.set(dateTime - Clock.systemUTC().instant().toEpochMilli());
    }
}
