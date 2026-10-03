package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ServiceRepositoryObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessContextObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessSessionObject;
import indi.sly.system.kernel.processes.values.ProcessContextType;
import indi.sly.system.kernel.services.instances.values.ServiceModeType;
import indi.sly.system.kernel.services.values.ServiceStatusEntity;
import indi.sly.system.services.core.environment.values.ServiceKernelExtensionSpace;
import indi.sly.system.services.core.prototypes.TransactionalActionComponent;
import indi.sly.system.services.jobs.lang.CallContextProcessorCreateFunction;
import indi.sly.system.services.jobs.prototypes.mediators.CallContextProcessorMediator;
import indi.sly.system.services.jobs.values.CallContextProcessType;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CallContextCreateCheckProcessResolver extends AResolver implements ICallContextCreateResolver {
    public CallContextCreateCheckProcessResolver() {
        this.create = (callContext, callContextRequest) -> {
            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);

            ServiceKernelExtensionSpace serviceSpace = (ServiceKernelExtensionSpace) this.coreManager.getKernelSpace().getServiceSpace();
            TransactionalActionComponent transactionalAction = serviceSpace.getTransactionalAction();

            transactionalAction.runWithTransactional(() -> {
                ProcessObject process = processManager.getCurrent();
                ProcessContextObject processContext = process.getContext();

                if (LogicalUtil.isAnyEqual(processContext.getType(), ProcessContextType.EXECUTABLE_SERVICE)) {
                    ServiceRepositoryObject serviceRepository = memoryManager.getServiceRepository();
                    ServiceStatusEntity serviceStatus = serviceRepository.get(processContext.getApplication().id());
                    long mode = serviceStatus.getMode();

                    if (LogicalUtil.isAnyEqual(mode, ServiceModeType.ONLY_APPLICATION) && LogicalUtil.allNotEqual(callContextRequest.process().processType(), CallContextProcessType.APPLICATION)) {
                        throw new ConditionRefuseException();
                    }
                }

                ProcessSessionObject processSession = process.getSession();

                UUID processSessionId = processSession.getId();
                if (!ValueUtil.isAnyNullOrEmpty(processSessionId) && !processSessionId.equals(callContextRequest.sessionId())) {
                    throw new ConditionRefuseException();
                }

                return null;
            });

            return callContext;
        };
    }

    private final CallContextProcessorCreateFunction create;

    @Override
    public void resolve(CallContextProcessorMediator processorMediator) {
        processorMediator.getCreates().add(this.create);
    }

    @Override
    public int order() {
        return 2;
    }
}
