package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.services.core.prototypes.TransactionalActionObject;
import indi.sly.system.services.job.lang.UserContextProcessorCreateFunction;
import indi.sly.system.services.job.prototypes.wrappers.UserContextProcessorMediator;
import indi.sly.system.services.job.values.UserContextRequestProcessIDRawDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextCreateProcessAndThreadResolver extends AUserContextCreateResolver {
    public UserContextCreateProcessAndThreadResolver() {
        this.create = (userContext, userContextRequestRaw) -> {
            UserContextRequestProcessIDRawDefinition processIDRaw = userContextRequestRaw.getProcessID();

//            userContentProcessIDRaw.getVerification();

            UUID processID = processIDRaw.getID();

            TransactionalActionObject transactionalAction = this.factoryManager.create(TransactionalActionObject.class);

            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
            ThreadObject thread = transactionalAction.runWithTransactional(() -> threadManager.create(processID));

            userContext.setThreadID(thread.getID());

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
        return 1;
    }
}
