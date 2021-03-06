package indi.sly.system.kernel.core;

import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.core.prototypes.CorePrototypeBuilder;
import indi.sly.system.kernel.core.prototypes.CoreRepositoryObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.security.AccountGroupManager;
import indi.sly.system.kernel.processes.SessionManager;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FactoryManager extends AManager {
    @Override
    public void startup(long startupTypes) {
        if (startupTypes == StartupType.STEP_INIT) {
            this.factoryManager = this;
            this.factoryManager.check();

            UserSpaceDefinition bootUserSpace = new UserSpaceDefinition();
            this.factoryManager.setUserSpaceContainer(() -> bootUserSpace);

            this.corePrototype = new CorePrototypeBuilder();
            this.corePrototype.setFactoryManager(this);

            this.coreRepository = this.create(CoreRepositoryObject.class);
            this.coreRepository.add(SpaceType.KERNEL, this.create(FactoryManager.class));
            this.coreRepository.add(SpaceType.KERNEL, this.create(MemoryManager.class));
            this.coreRepository.add(SpaceType.KERNEL, this.create(ProcessManager.class));
            this.coreRepository.add(SpaceType.KERNEL, this.create(ThreadManager.class));
            this.coreRepository.add(SpaceType.KERNEL, this.create(TypeManager.class));
            this.coreRepository.add(SpaceType.KERNEL, this.create(ObjectManager.class));
            this.coreRepository.add(SpaceType.KERNEL, this.create(AccountGroupManager.class));
            this.coreRepository.add(SpaceType.KERNEL, this.create(SessionManager.class));
            // ...
            this.coreRepository.add(SpaceType.KERNEL, this.create(DateTimeObject.class));
            this.coreRepository.add(SpaceType.KERNEL, this.create(CoreRepositoryObject.class));
        } else if (startupTypes == StartupType.STEP_KERNEL) {
        }
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void check() {
        if (ObjectUtil.isAnyNull(this.factoryManager)) {
            throw new StatusNotReadyException();
        }
    }

    private CorePrototypeBuilder corePrototype;
    private CoreRepositoryObject coreRepository;

    public <T extends APrototype> T create(Class<T> clazz) {
        return this.corePrototype.create(clazz);
    }

    public CoreRepositoryObject getCoreRepository() {
        return this.coreRepository;
    }

    public <T extends AManager> T getManager(Class<T> clazz) {
        T manager = this.coreRepository.get(SpaceType.KERNEL, clazz);

        manager.check();

        return manager;
    }

    public KernelSpaceDefinition getKernelSpace() {
        return SpringHelper.getInstance(KernelSpaceDefinition.class);
    }

    private Provider<UserSpaceDefinition> userSpaceContainer;

    public UserSpaceDefinition getUserSpace() {
        return this.userSpaceContainer == null ? null : this.userSpaceContainer.acquire();
    }

    public void setUserSpaceContainer(Provider<UserSpaceDefinition> userSpaceContainer) {
        this.userSpaceContainer = userSpaceContainer;
    }
}
