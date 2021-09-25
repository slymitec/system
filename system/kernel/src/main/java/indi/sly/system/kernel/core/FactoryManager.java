package indi.sly.system.kernel.core;

import indi.sly.system.common.lang.ConditionContextException;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.kernel.core.boot.prototypes.BootFactory;
import indi.sly.system.kernel.core.boot.prototypes.BootObject;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.core.prototypes.CoreObjectRepositoryObject;
import indi.sly.system.kernel.core.prototypes.CorePrototypeValueBuilder;
import indi.sly.system.kernel.core.prototypes.SystemVersionObject;
import indi.sly.system.kernel.files.FileSystemManager;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.security.UserManager;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FactoryManager extends AManager {
    protected BootFactory bootFactory;

    @Override
    public void startup(long startup) {
        if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_SELF)) {
            this.factoryManager = this;
            this.factoryManager.check();

            this.corePrototypeValueBuilder = SpringHelper.getInstance(CorePrototypeValueBuilder.class);
            this.corePrototypeValueBuilder.setFactoryManager(this);

            this.setUserSpace(new UserSpaceDefinition());

            this.coreObjectRepository = this.factoryManager.create(CoreObjectRepositoryObject.class);
            this.coreObjectRepository.setLimit(SpaceType.KERNEL, Long.MAX_VALUE);
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this);
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(FileSystemManager.class));
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(MemoryManager.class));
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(ObjectManager.class));
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(ProcessManager.class));
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(ThreadManager.class));
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(TypeManager.class));
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(UserManager.class));
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(DateTimeObject.class));
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, this.create(SystemVersionObject.class));
            this.bootFactory = this.factoryManager.create(BootFactory.class);
            this.bootFactory.init();
            BootObject boot = this.bootFactory.buildBoot();
            this.coreObjectRepository.addByClass(SpaceType.KERNEL, boot);
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

    private CorePrototypeValueBuilder corePrototypeValueBuilder;
    private CoreObjectRepositoryObject coreObjectRepository;

    public CoreObjectRepositoryObject getCoreObjectRepository() {
        return this.coreObjectRepository;
    }

    public <T extends APrototype> T create(Class<T> clazz) {
        return this.corePrototypeValueBuilder.createPrototype(clazz);
    }

    public <T extends AManager> T getManager(Class<T> clazz) {
        T manager = this.coreObjectRepository.getByClass(SpaceType.KERNEL, clazz);

        manager.check();

        return manager;
    }

    public <T extends AService> T getService(Class<T> clazz) {
        T service = this.coreObjectRepository.getByClass(SpaceType.KERNEL, clazz);

        service.check();

        return service;
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
