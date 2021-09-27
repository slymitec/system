package indi.sly.system.services.auxiliary.prototypes.processors;

import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.services.auxiliary.lang.UserContextProcessorCreateFunction;
import indi.sly.system.services.auxiliary.prototypes.wrappers.AuxiliaryProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextCreateUserSpace extends AUserContextCreateResolver {
    public UserContextCreateUserSpace() {
        this.create = (userContext, userContextRequestRaw) -> {
            KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();
            KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

            this.factoryManager.setUserSpace(new UserSpaceDefinition());
            this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);

            return userContext;
        };
    }

    private final UserContextProcessorCreateFunction create;

    @Override
    public void resolve(AuxiliaryProcessorMediator processorMediator) {
        processorMediator.getCreates().add(this.create);
    }

    @Override
    public int order() {
        return 0;
    }
}
