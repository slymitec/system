package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ServiceRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessContextObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.values.ProcessContextType;
import indi.sly.system.kernel.services.instances.values.ServiceModeType;
import indi.sly.system.kernel.services.values.ServiceStatusEntity;
import indi.sly.system.services.jobs.lang.UserContextProcessorCreateFunction;
import indi.sly.system.services.jobs.prototypes.mediators.UserContextProcessorMediator;
import indi.sly.system.services.jobs.values.ClientRequestProcessIdDefinition;
import indi.sly.system.services.jobs.values.ClientRequestType;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextCreateCheckProcessResolver extends AResolver implements IUserContextCreateResolver {
    public UserContextCreateCheckProcessResolver() {
        this.create = (userContext, userContextRequest) -> {
            ClientRequestProcessIdDefinition userContextRequestProcessId = userContextRequest.getProcessId();

            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

            ProcessObject process = processManager.getCurrent();
            ProcessContextObject processContext = process.getContext();

            if (LogicalUtil.isAnyEqual(processContext.getType(), ProcessContextType.EXECUTABLE_SERVICE)) {
                ServiceRepositoryObject serviceRepository = memoryManager.getServiceRepository();
                ServiceStatusEntity serviceStatus = serviceRepository.get(processContext.getApplication().id());
                long mode = serviceStatus.getMode();

                if (LogicalUtil.isAnyEqual(mode, ServiceModeType.ONLY_APPLICATION) && LogicalUtil.allNotEqual(userContextRequestProcessId.getType(), ClientRequestType.APPLICATION)) {
                    throw new ConditionRefuseException();
                }
            }

            return userContext;
        };
    }

    private final UserContextProcessorCreateFunction create;

    @Override
    public void resolve(UserContextProcessorMediator processorMediator) {
        processorMediator.getCreates().add(this.create);
    }

    @Override
    public int order() {
        return 3;
    }
}
