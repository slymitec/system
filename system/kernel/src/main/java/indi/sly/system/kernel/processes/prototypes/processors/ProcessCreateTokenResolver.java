package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorCreateFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreateTokenResolver extends AProcessCreateResolver {
    private final ProcessLifeProcessorCreateFunction create;

    public ProcessCreateTokenResolver() {
        this.create = (process, parentProcess, processCreator) -> {
            ProcessTokenObject processToken = process.getToken();
            AccountAuthorizationObject accountAuthorization = processCreator.getAccountAuthorization();
            if (ObjectUtil.allNotNull(accountAuthorization) && accountAuthorization.isLegal()) {
                processToken.setAccountAuthorization(accountAuthorization);
            } else {
                processToken.inheritAccountID();
                processToken.inheritPrivileges();
                processToken.inheritLimits();
            }

            return process;
        };
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public void resolve(ProcessLifeProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getCreates().add(this.create);
    }
}
