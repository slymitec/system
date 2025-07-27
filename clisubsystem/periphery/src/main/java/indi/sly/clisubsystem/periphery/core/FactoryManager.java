package indi.sly.clisubsystem.periphery.core;

import indi.sly.clisubsystem.periphery.core.boot.prototypes.BootFactory;
import indi.sly.clisubsystem.periphery.core.boot.prototypes.BootObject;
import indi.sly.clisubsystem.periphery.core.boot.values.StartupType;
import indi.sly.clisubsystem.periphery.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.clisubsystem.periphery.core.enviroment.values.SpaceType;
import indi.sly.clisubsystem.periphery.core.enviroment.values.UserSpaceDefinition;
import indi.sly.clisubsystem.periphery.core.prototypes.APrototype;
import indi.sly.clisubsystem.periphery.core.prototypes.CoreObjectRepositoryObject;
import indi.sly.clisubsystem.periphery.core.prototypes.CorePrototypeValueBuilder;
import indi.sly.system.common.lang.ConditionContextException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

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

            this.coreObjectRepository = this.factoryManager.create(CoreObjectRepositoryObject.class);
            this.coreObjectRepository.setLimit(SpaceType.KERNEL, Long.MAX_VALUE);
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

    public KernelSpaceDefinition getKernelSpace() {
        return SpringHelper.getInstance(KernelSpaceDefinition.class);
    }

    public UserSpaceDefinition getUserSpace() {
        return this.getKernelSpace().getUserSpace();
    }
}
