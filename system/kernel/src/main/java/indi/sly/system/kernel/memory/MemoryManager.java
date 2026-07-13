package indi.sly.system.kernel.memory;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.environment.values.SpaceType;
import indi.sly.system.kernel.memory.repositories.prototypes.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MemoryManager extends AManager {
    @Override
    public void startup(long startup) {
        if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_AFTER_SELF)) {
            this.coreManager.getObjectCollection().addById(SpaceType.KERNEL,
                    this.coreManager.getKernelSpace().getConfiguration().MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORY_ID,
                    this.coreManager.create(DatabaseInfoRepositoryObject.class));

            this.coreManager.getObjectCollection().addByClass(SpaceType.KERNEL,
                    this.coreManager.create(CacheRepositoryObject.class));
            this.coreManager.getObjectCollection().addByClass(SpaceType.KERNEL,
                    this.coreManager.create(DistributionRepositoryObject.class));
            this.coreManager.getObjectCollection().addByClass(SpaceType.KERNEL,
                    this.coreManager.create(ProcessRepositoryObject.class));
            this.coreManager.getObjectCollection().addByClass(SpaceType.KERNEL,
                    this.coreManager.create(ServiceRepositoryObject.class));
            this.coreManager.getObjectCollection().addByClass(SpaceType.KERNEL,
                    this.coreManager.create(UserRepositoryObject.class));
        }
    }

    @Override
    public void shutdown() {
    }

    public AInfoRepositoryObject getInfoRepository(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        return this.coreManager.getObjectCollection().getById(SpaceType.KERNEL, id);
    }

    public ProcessRepositoryObject getProcessRepository() {
        return this.coreManager.getObjectCollection().getByClass(SpaceType.KERNEL, ProcessRepositoryObject.class);
    }

    public UserRepositoryObject getUserRepository() {
        return this.coreManager.getObjectCollection().getByClass(SpaceType.KERNEL, UserRepositoryObject.class);
    }

    public CacheRepositoryObject getCacheRepository() {
        return this.coreManager.getObjectCollection().getByClass(SpaceType.KERNEL, CacheRepositoryObject.class);
    }

    public DistributionRepositoryObject getDistributionRepository() {
        return this.coreManager.getObjectCollection().getByClass(SpaceType.KERNEL, DistributionRepositoryObject.class);
    }

    public ServiceRepositoryObject getServiceRepository() {
        return this.coreManager.getObjectCollection().getByClass(SpaceType.KERNEL, ServiceRepositoryObject.class);
    }
}
