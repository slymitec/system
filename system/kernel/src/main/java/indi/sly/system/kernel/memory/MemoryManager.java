package indi.sly.system.kernel.memory;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.memory.repositories.prototypes.UserRepositoryObject;
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
    public void startup(long startup) {
        if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_AFTER_SELF)) {
            this.factoryManager.getCoreObjectRepository().addByHandle(SpaceType.KERNEL,
                    this.factoryManager.getKernelSpace().getConfiguration().MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID,
                    this.factoryManager.create(DatabaseInfoRepositoryObject.class));

            this.factoryManager.getCoreObjectRepository().addByClass(SpaceType.KERNEL,
                    this.factoryManager.create(ProcessRepositoryObject.class));
            this.factoryManager.getCoreObjectRepository().addByClass(SpaceType.KERNEL,
                    this.factoryManager.create(UserRepositoryObject.class));
        }
    }

    @Override
    public void shutdown() {
    }

    public AInfoRepositoryObject getInfoRepository(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        return this.factoryManager.getCoreObjectRepository().getByHandle(SpaceType.KERNEL, id);
    }

    public ProcessRepositoryObject getProcessRepository() {
        return this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, ProcessRepositoryObject.class);
    }

    public UserRepositoryObject getUserRepository() {
        return this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, UserRepositoryObject.class);
    }
}
