package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorCreateFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.processes.prototypes.mediators.ProcessLifeProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessContextType;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreateCheckResolver extends AResolver implements IProcessCreateResolver {
    private final ProcessLifeProcessorCreateFunction create;

    public ProcessCreateCheckResolver() {
        this.create = (process, parentProcess, processCreator) -> {
            AccountAuthorizationObject accountAuthorization = processCreator.getAccountAuthorization();
            if (ObjectUtil.allNotNull(accountAuthorization)) {
                accountAuthorization.checkAndGetSummary();
            }

            ProcessTokenObject parentProcessToken = parentProcess.getToken();
            if ((!processCreator.isInheritSession() && !parentProcessToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_SESSION))
                    || (LogicalUtil.isAnyEqual(processCreator.getContextType(), ProcessContextType.EXECUTABLE_SERVICE) && !parentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY))) {
                throw new ConditionRefuseException();
            }

            return process;
        };
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public void resolve(ProcessLifeProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getCreates().add(this.create);
    }
}
