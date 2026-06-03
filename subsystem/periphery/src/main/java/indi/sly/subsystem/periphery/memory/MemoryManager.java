package indi.sly.subsystem.periphery.memory;

import indi.sly.subsystem.periphery.core.AManager;
import indi.sly.subsystem.periphery.core.boot.values.StartupType;
import indi.sly.subsystem.periphery.core.enviroment.values.SpaceType;
import indi.sly.subsystem.periphery.memory.repositories.prototypes.CacheRepositoryObject;
import indi.sly.subsystem.periphery.memory.repositories.prototypes.DistributionRepositoryObject;
import indi.sly.system.common.supports.LogicalUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MemoryManager extends AManager {
    @Override
    public void startup(long startup) {
        if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_AFTER_SELF)) {
            this.coreManager.getObjectCollection().addByClass(SpaceType.KERNEL,
                    this.coreManager.create(CacheRepositoryObject.class));
            this.coreManager.getObjectCollection().addByClass(SpaceType.KERNEL,
                    this.coreManager.create(DistributionRepositoryObject.class));
        }
    }

    @Override
    public void shutdown() {
    }

    public CacheRepositoryObject getCacheRepository() {
        return this.coreManager.getObjectCollection().getByClass(SpaceType.KERNEL, CacheRepositoryObject.class);
    }

    public DistributionRepositoryObject getDistributionRepository() {
        return this.coreManager.getObjectCollection().getByClass(SpaceType.KERNEL, DistributionRepositoryObject.class);
    }
}
