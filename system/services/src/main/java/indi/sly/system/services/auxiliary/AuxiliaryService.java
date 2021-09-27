package indi.sly.system.services.auxiliary;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.AService;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.services.auxiliary.prototypes.UserContextBuilder;
import indi.sly.system.services.auxiliary.prototypes.AuxiliaryFactory;
import indi.sly.system.services.auxiliary.prototypes.UserContextObject;
import indi.sly.system.services.core.environment.values.ServiceKernelSpaceExtensionDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuxiliaryService extends AService {
    @Override
    public void startup(long startup) {
        if (startup == StartupType.STEP_INIT_SELF) {
            this.factoryManager.getKernelSpace().setServiceSpace(new ServiceKernelSpaceExtensionDefinition());

            this.factory = this.factoryManager.create(AuxiliaryFactory.class);
            this.factory.init();
        }
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void check() {
    }

    protected AuxiliaryFactory factory;

    public UserContextObject create(String userRequest) {
        if (ValueUtil.isAnyNullOrEmpty(userRequest)) {
            throw new ConditionParametersException();
        }

        UserContextBuilder userContextBuilder = this.factory.createUserContext();

        return userContextBuilder.create(userRequest);
    }

    //维护 UserContentDefinition
    //建立销毁 Thread
    //维护 UserSpaceDefinition
}
