package indi.sly.system.kernel.memory;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.StartupTypes;
import indi.sly.system.kernel.core.enviroment.SpaceTypes;
import indi.sly.system.kernel.memory.caches.InfoObjectCacheObject;
import indi.sly.system.kernel.memory.repositories.AEntityRepositoryObject;
import indi.sly.system.kernel.memory.repositories.DatabaseEntityRepositoryObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MemoryManager extends AManager {
    @Override
    public void startup(long startupTypes) {
        if (startupTypes == StartupTypes.STEP_INIT) {
            this.factoryManager.getCoreObjectRepository().add(SpaceTypes.KERNEL, ObjectUtils.getObject(InfoObjectCacheObject.class));
            this.factoryManager.getCoreObjectRepository().addByID(SpaceTypes.KERNEL, this.factoryManager.getKernelSpace().getConfiguration().MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID, ObjectUtils.getObject(DatabaseEntityRepositoryObject.class));
        } else if (startupTypes == StartupTypes.STEP_KERNEL) {
        }
    }

    public AEntityRepositoryObject getEntityRepository(UUID id) {
        if (UUIDUtils.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        return this.factoryManager.getCoreObjectRepository().getByID(SpaceTypes.KERNEL, AEntityRepositoryObject.class, id);
    }
}
