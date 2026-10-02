package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.services.core.environment.values.ServiceKernelExtensionSpace;
import indi.sly.system.services.core.prototypes.TransactionalActionComponent;
import indi.sly.system.services.jobs.lang.CallContextProcessorCreateFunction;
import indi.sly.system.services.jobs.prototypes.mediators.CallContextProcessorMediator;
import indi.sly.system.services.jobs.values.CallContextRecord;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CallContextCreateThreadResolver extends AResolver implements ICallContextCreateResolver {
    public CallContextCreateThreadResolver() {
        this.create = (callContext, callContextRequest) -> {
            UUID processId = callContextRequest.process().processId();

            ServiceKernelExtensionSpace serviceSpace = (ServiceKernelExtensionSpace) this.coreManager.getKernelSpace().getServiceSpace();
            TransactionalActionComponent transactionalAction = serviceSpace.getTransactionalAction();

            ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
            ThreadObject thread = transactionalAction.runWithTransactional(() -> threadManager.create(processId));

            callContext.setThreadId(thread.getId());

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
        return 1;
    }
}
