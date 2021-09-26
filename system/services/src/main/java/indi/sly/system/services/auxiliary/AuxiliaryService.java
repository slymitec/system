package indi.sly.system.services.auxiliary;

import indi.sly.system.kernel.core.AService;
import indi.sly.system.kernel.core.boot.values.StartupType;
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
        }
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void check() {
    }

    //维护 UserContentDefinition
    //建立销毁 Thread
    //维护 UserSpaceDefinition
}
