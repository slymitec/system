package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.services.core.prototypes.TransactionalActionComponent;
import indi.sly.system.services.jobs.lang.UserContextProcessorCreateFunction;
import indi.sly.system.services.jobs.prototypes.mediators.UserContextProcessorMediator;
import indi.sly.system.services.jobs.values.ClientRequestProcessIdDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextCreateThreadResolver extends AResolver implements IUserContextCreateResolver {
    public UserContextCreateThreadResolver() {
        this.create = (userContext, userContextRequest) -> {
            ClientRequestProcessIdDefinition userContextRequestProcessId = userContextRequest.getProcessId();

            UUID processId = userContextRequestProcessId.getId();

            TransactionalActionComponent transactionalAction = this.coreManager.create(TransactionalActionComponent.class);

            ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
            ThreadObject thread = transactionalAction.runWithTransactional(() -> threadManager.create(processId));

            userContext.setThreadId(thread.getId());

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
        return 2;
    }
}
