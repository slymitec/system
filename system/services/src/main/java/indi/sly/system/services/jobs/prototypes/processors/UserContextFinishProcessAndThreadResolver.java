package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.services.core.prototypes.TransactionalActionObject;
import indi.sly.system.services.jobs.lang.UserContextProcessorFinishFunction;
import indi.sly.system.services.jobs.prototypes.wrappers.UserContextProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextFinishProcessAndThreadResolver extends AUserContextFinishResolver {
    public UserContextFinishProcessAndThreadResolver() {
        this.finish = (userContext) -> {
            TransactionalActionObject transactionalAction = this.factoryManager.create(TransactionalActionObject.class);

            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
            transactionalAction.runWithTransactional(() -> {
                if (threadManager.size() == 0) {
                    throw new StatusRelationshipErrorException();
                }

                ThreadObject thread = threadManager.getCurrent();

                if (!thread.getID().equals(userContext.getThreadID())) {
                    throw new StatusRelationshipErrorException();
                }

                threadManager.end();

                return null;
            });

            return userContext;
        };
    }

    private final UserContextProcessorFinishFunction finish;

    @Override
    public void resolve(UserContextProcessorMediator processorMediator) {
        processorMediator.getFinishes().add(this.finish);
    }

    @Override
    public int order() {
        return 0;
    }
}
