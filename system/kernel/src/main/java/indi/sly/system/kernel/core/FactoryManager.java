package indi.sly.system.kernel.core;

import indi.sly.system.common.lang.ConditionContextException;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.core.prototypes.CorePrototypeValueBuilder;
import indi.sly.system.kernel.core.prototypes.CorePrototypeRepositoryObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.processes.SessionManager;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FactoryManager extends AManager {
    @Override
    public void startup(long startup) {
        if (startup == StartupType.STEP_INIT) {
            this.factoryManager = this;
            this.factoryManager.check();

            this.corePrototypeValue = SpringHelper.getInstance(CorePrototypeValueBuilder.class);
            this.corePrototypeValue.setFactoryManager(this);

            this.userSpace = () -> this.corePrototypeValue.createOrGetUserSpace();

            this.corePrototypeRepository = this.create(CorePrototypeRepositoryObject.class);
            this.corePrototypeRepository.add(SpaceType.KERNEL, this.create(FactoryManager.class));
            this.corePrototypeRepository.add(SpaceType.KERNEL, this.create(MemoryManager.class));
            this.corePrototypeRepository.add(SpaceType.KERNEL, this.create(ProcessManager.class));
            this.corePrototypeRepository.add(SpaceType.KERNEL, this.create(ThreadManager.class));
            this.corePrototypeRepository.add(SpaceType.KERNEL, this.create(TypeManager.class));
            this.corePrototypeRepository.add(SpaceType.KERNEL, this.create(ObjectManager.class));
            this.corePrototypeRepository.add(SpaceType.KERNEL, this.create(UserManager.class));
            this.corePrototypeRepository.add(SpaceType.KERNEL, this.create(SessionManager.class));

            this.corePrototypeRepository.add(SpaceType.KERNEL, this.create(DateTimeObject.class));
            this.corePrototypeRepository.add(SpaceType.KERNEL, this.create(CorePrototypeRepositoryObject.class));
        } else if (startup == StartupType.STEP_KERNEL) {
        }
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void check() {
        if (ObjectUtil.isAnyNull(this.factoryManager)) {
            throw new ConditionContextException();
        }
    }

    private CorePrototypeValueBuilder corePrototypeValue;
    private CorePrototypeRepositoryObject corePrototypeRepository;

    public CorePrototypeRepositoryObject getCorePrototypeRepository() {
        return this.corePrototypeRepository;
    }

    public <T extends APrototype> T create(Class<T> clazz) {
        return this.corePrototypeValue.createPrototype(clazz);
    }

    public <T extends AManager> T getManager(Class<T> clazz) {
        T manager = this.corePrototypeRepository.get(SpaceType.KERNEL, clazz);

        manager.check();

        return manager;
    }

    public <T extends AService> T getService(Class<T> clazz) {
        T manager = this.corePrototypeRepository.get(SpaceType.KERNEL, clazz);

        manager.check();

        return manager;
    }

    public KernelSpaceDefinition getKernelSpace() {
        return SpringHelper.getInstance(KernelSpaceDefinition.class);
    }

    private Provider<UserSpaceDefinition> userSpace;

    public UserSpaceDefinition getUserSpace() {
        return this.userSpace.acquire();
    }

    public void setUserSpace(Provider<UserSpaceDefinition> userSpace) {
        if (ObjectUtil.isAnyNull(userSpace)) {
            throw new ConditionParametersException();
        }

        this.userSpace = userSpace;
    }
}
