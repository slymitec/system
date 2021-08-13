package indi.sly.system.kernel.core;

import indi.sly.system.common.lang.ConditionContextException;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.core.prototypes.*;
import indi.sly.system.kernel.files.FileSystemManager;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.SessionManager;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.security.UserManager;
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

            this.setUserSpace(new UserSpaceDefinition());

            this.coreObjectRepository = this.create(CoreObjectRepositoryObject.class);
            this.coreObjectRepository.setLimit(SpaceType.KERNEL, Long.MAX_VALUE);
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(FactoryManager.class));
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(FileSystemManager.class));
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(MemoryManager.class));
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(ObjectManager.class));
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(ProcessManager.class));
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(SessionManager.class));
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(ThreadManager.class));
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(TypeManager.class));
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(UserManager.class));
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(DateTimeObject.class));
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(SystemVersionObject.class));
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
    private CoreObjectRepositoryObject coreObjectRepository;

    public CoreObjectRepositoryObject getCoreObjectRepository() {
        return this.coreObjectRepository;
    }

    public <T extends APrototype> T create(Class<T> clazz) {
        return this.corePrototypeValue.createPrototype(clazz);
    }

    public <T extends AManager> T getManager(Class<T> clazz) {
        T manager = this.coreObjectRepository.getByClass(SpaceType.KERNEL, clazz);

        manager.check();

        return manager;
    }

    public <T extends AService> T getService(Class<T> clazz) {
        T manager = this.coreObjectRepository.getByClass(SpaceType.KERNEL, clazz);

        manager.check();

        return manager;
    }

    public KernelSpaceDefinition getKernelSpace() {
        return SpringHelper.getInstance(KernelSpaceDefinition.class);
    }

    public UserSpaceDefinition getUserSpace() {
        return this.getKernelSpace().getUserSpaces().get();
    }

    public void setUserSpace(UserSpaceDefinition userSpace) {
        if (ObjectUtil.isAnyNull(userSpace)) {
            throw new ConditionParametersException();
        }

        this.getKernelSpace().getUserSpaces().set(userSpace);
    }
}
