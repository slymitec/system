package indi.sly.system.kernel.memory;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.memory.caches.prototypes.InfoCacheObject;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.memory.repositories.prototypes.AccountGroupRepositoryObject;
import indi.sly.system.kernel.memory.repositories.prototypes.DatabaseInfoRepositoryObject;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MemoryManager extends AManager {
    @Override
    public void startup(long startupTypes) {
        if (startupTypes == StartupType.STEP_INIT) {
            this.factoryManager.getCoreRepository().add(SpaceType.KERNEL,
                    this.factoryManager.create(InfoCacheObject.class));
            this.factoryManager.getCoreRepository().addByID(SpaceType.KERNEL,
                    this.factoryManager.getKernelSpace().getConfiguration().MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID,
                    this.factoryManager.create(DatabaseInfoRepositoryObject.class));

            this.factoryManager.getCoreRepository().add(SpaceType.KERNEL,
                    this.factoryManager.create(ProcessRepositoryObject.class));
            this.factoryManager.getCoreRepository().add(SpaceType.KERNEL,
                    this.factoryManager.create(AccountGroupRepositoryObject.class));
        } else if (startupTypes == StartupType.STEP_KERNEL) {
        }
    }

    public AInfoRepositoryObject getInfoRepository(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        return this.factoryManager.getCoreRepository().getByID(SpaceType.KERNEL, AInfoRepositoryObject.class,
                id);
    }

    public ProcessRepositoryObject getProcessRepository() {
        return this.factoryManager.getCoreRepository().get(SpaceType.KERNEL, ProcessRepositoryObject.class);
    }

    public AccountGroupRepositoryObject getAccountGroupRepository() {
        return this.factoryManager.getCoreRepository().get(SpaceType.KERNEL, AccountGroupRepositoryObject.class);
    }
}
