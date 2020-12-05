package indi.sly.system.kernel.core;

import indi.sly.system.common.exceptions.StatusRelationshipErrorException;
import indi.sly.system.common.functions.Provider;
import indi.sly.system.common.utility.SpringUtils;
import indi.sly.system.kernel.core.boot.types.StartupTypes;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.enviroment.KernelSpace;
import indi.sly.system.kernel.core.enviroment.types.SpaceTypes;
import indi.sly.system.kernel.core.enviroment.UserSpace;
import indi.sly.system.kernel.core.prototypes.ACorePrototype;
import indi.sly.system.kernel.core.prototypes.CorePrototypeBuilder;
import indi.sly.system.kernel.core.prototypes.CoreRepositoryObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.security.SecurityTokenManager;
import indi.sly.system.kernel.sessions.SessionManager;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FactoryManager extends AManager {
    @Override
    public void startup(long startupTypes) {
        if (startupTypes == StartupTypes.STEP_INIT) {
            this.factoryManager = this;

            UserSpace bootUserSpace = new UserSpace();
            this.factoryManager.setUserSpaceContainer(() -> {
                return bootUserSpace;
            });

            this.corePrototype = new CorePrototypeBuilder();
            this.corePrototype.setFactoryManager(this);

            this.coreRepository = this.create(CoreRepositoryObject.class);
            this.coreRepository.add(SpaceTypes.KERNEL, this.create(FactoryManager.class));
            this.coreRepository.add(SpaceTypes.KERNEL, this.create(MemoryManager.class));
            this.coreRepository.add(SpaceTypes.KERNEL, this.create(ProcessManager.class));
            this.coreRepository.add(SpaceTypes.KERNEL, this.create(ThreadManager.class));
            this.coreRepository.add(SpaceTypes.KERNEL, this.create(TypeManager.class));
            this.coreRepository.add(SpaceTypes.KERNEL, this.create(ObjectManager.class));
            this.coreRepository.add(SpaceTypes.KERNEL, this.create(SecurityTokenManager.class));
            this.coreRepository.add(SpaceTypes.KERNEL, this.create(SessionManager.class));
            // ...
            this.coreRepository.add(SpaceTypes.KERNEL, this.create(DateTimeObject.class));
            this.coreRepository.add(SpaceTypes.KERNEL, this.create(CoreRepositoryObject.class));
        } else if (startupTypes == StartupTypes.STEP_KERNEL) {
        }
    }

    private CorePrototypeBuilder corePrototype;
    private CoreRepositoryObject coreRepository;

    public <T extends ACorePrototype> T create(Class<T> clazz) {
        return this.corePrototype.create(clazz);
    }

    public CoreRepositoryObject getCoreRepository() {
        return this.coreRepository;
    }

    @SuppressWarnings("unchecked")
    public <T extends AManager> T getManager(Class<T> clazz) {
        T corePrototype = this.coreRepository.get(SpaceTypes.KERNEL, clazz);
        if (!(corePrototype instanceof AManager)) {
            throw new StatusRelationshipErrorException();
        }
        return corePrototype;
    }

    public KernelSpace getKernelSpace() {
        return SpringUtils.getInstance(KernelSpace.class);
    }

    private Provider<UserSpace> userSpaceContainer;

    public UserSpace getUserSpace() {
        return this.userSpaceContainer == null ? null : this.userSpaceContainer.acquire();
    }

    public void setUserSpaceContainer(Provider<UserSpace> userSpaceContainer) {
        this.userSpaceContainer = userSpaceContainer;
    }
}
