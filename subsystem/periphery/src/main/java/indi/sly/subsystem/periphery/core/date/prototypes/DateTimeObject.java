package indi.sly.subsystem.periphery.core.date.prototypes;

import indi.sly.subsystem.periphery.core.environment.containers.KernelSpace;
import indi.sly.subsystem.periphery.core.environment.containers.PeripheryConfiguration;
import indi.sly.subsystem.periphery.core.prototypes.ACacheableObject;
import indi.sly.subsystem.periphery.core.values.NoneCacheEntity;
import indi.sly.subsystem.periphery.memory.MemoryManager;
import indi.sly.subsystem.periphery.memory.repositories.prototypes.DistributionRepositoryObject;

import jakarta.inject.Named;
import org.redisson.api.RBucket;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.time.Clock;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DateTimeObject extends ACacheableObject<NoneCacheEntity> {
    public long getCurrent() {
        KernelSpace kernelSpace = this.coreManager.getKernelSpace();
        PeripheryConfiguration peripheryConfiguration = kernelSpace.getConfiguration();

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        DistributionRepositoryObject distributionRepository = memoryManager.getDistributionRepository();

        RBucket<Long> systemTimeOffset = distributionRepository.getBucket("DateTime", peripheryConfiguration.CORE_PROTOTYPE_DATETIME_SYSTEM_TIME_OFFSET, null);

        if (systemTimeOffset.isExists()) {
            return Clock.systemUTC().instant().toEpochMilli() + systemTimeOffset.get();
        } else {
            return Clock.systemUTC().instant().toEpochMilli();
        }
    }

    public void correct(long dateTime) {
        KernelSpace kernelSpace = this.coreManager.getKernelSpace();
        PeripheryConfiguration peripheryConfiguration = kernelSpace.getConfiguration();

        MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
        DistributionRepositoryObject distributionRepository = memoryManager.getDistributionRepository();

        RBucket<Long> systemTimeOffset = distributionRepository.getBucket("DateTime", peripheryConfiguration.CORE_PROTOTYPE_DATETIME_SYSTEM_TIME_OFFSET, null);

        systemTimeOffset.set(dateTime - Clock.systemUTC().instant().toEpochMilli());
    }
}
