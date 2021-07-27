package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.lang.CreateProcessFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessCreatorProcessorMediator;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreateProcessTokenResolver extends APrototype implements IProcessCreatorResolver {
    private final CreateProcessFunction createProcessFunction;

    public CreateProcessTokenResolver() {
        this.createProcessFunction = (process, parentProcess, processCreator) -> {
            ProcessTokenObject processToken = process.getToken();
            AccountAuthorizationObject accountAuthorization = processCreator.getAccountAuthorization();
            if (ObjectUtil.allNotNull(accountAuthorization) && accountAuthorization.isAccountIDLegal()) {
                processToken.setAccountAuthorization(accountAuthorization);
            } else {
                processToken.inheritAccountID();

                if (processCreator.getPrivileges() != PrivilegeType.NULL) {
                    processToken.inheritPrivileges(processCreator.getPrivileges());
                } else {
                    processToken.inheritPrivileges();
                }
                if (ObjectUtil.allNotNull(processCreator.getLimits())) {
                    processToken.setLimits(processCreator.getLimits());
                } else {
                    processToken.inheritLimits();
                }
            }

            return process;
        };
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public void resolve(ProcessCreatorProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getCreates().add(createProcessFunction);
    }
}
