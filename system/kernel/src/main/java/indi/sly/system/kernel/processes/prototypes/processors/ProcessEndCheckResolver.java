package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorCreateFunction;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorEndFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessContextObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.processes.prototypes.mediators.ProcessLifeProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessContextType;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.values.PrivilegeType;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessEndCheckResolver extends AResolver implements IProcessEndResolver {
    private final ProcessLifeProcessorEndFunction end;

    public ProcessEndCheckResolver() {
        this.end = (process, parentProcess) -> {
            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject currentProcess = processManager.getCurrent();

            if (!currentProcess.getId().equals(process.getId())) {
                ProcessTokenObject currentProcessToken = currentProcess.getToken();

                ProcessContextObject processContext = process.getContext();

                if (!currentProcessToken.isPrivileges(PrivilegeType.SECURITY_DO_WITH_ANY_ACCOUNT)
                        || (LogicalUtil.isAnyEqual(processContext.getType(), ProcessContextType.EXECUTABLE_SERVICE) && !currentProcessToken.isPrivileges(PrivilegeType.SERVICE_MODIFY))) {
                    throw new ConditionRefuseException();
                }
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
        processorCreatorMediator.getEnds().add(this.end);
    }
}
