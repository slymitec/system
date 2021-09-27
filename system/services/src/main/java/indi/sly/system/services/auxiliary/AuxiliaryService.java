package indi.sly.system.services.auxiliary;

import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.AService;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.services.auxiliary.prototypes.AuxiliaryFactory;
import indi.sly.system.services.auxiliary.prototypes.UserContextCreateBuilder;
import indi.sly.system.services.auxiliary.prototypes.UserContextFinishBuilder;
import indi.sly.system.services.auxiliary.prototypes.UserContextObject;
import indi.sly.system.services.auxiliary.values.UserContextRequestRawDefinition;
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

    public UserContextObject create(String userContextRequest) {
        UserContextRequestRawDefinition userContextRequestRaw;

        if (ValueUtil.isAnyNullOrEmpty(userContextRequest)) {
            throw new StatusUnreadableException();
        }

        try {
            userContextRequestRaw = ObjectUtil.transferFromString(UserContextRequestRawDefinition.class, userContextRequest);
        } catch (RuntimeException ignored) {
            throw new StatusUnreadableException();
        }

        UserContextCreateBuilder userContextCreateBuilder = this.factory.createUserContextCreator();

        return userContextCreateBuilder.build(userContextRequestRaw);
    }

    public void finish(UserContextObject userContext){
        UserContextFinishBuilder userContextFinishBuilder = this.factory.createUserContextFinish();

        userContextFinishBuilder.build(userContext);
    }
}
