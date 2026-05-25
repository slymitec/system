package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.services.core.prototypes.TransactionalActionComponent;
import indi.sly.system.services.jobs.lang.UserContextProcessorFinishFunction;
import indi.sly.system.services.jobs.prototypes.mediators.UserContextProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextFinishThreadResolver extends AResolver implements IUserContextFinishResolver {
    public UserContextFinishThreadResolver() {
        this.finish = (userContext) -> {
            TransactionalActionComponent transactionalAction = this.coreManager.create(TransactionalActionComponent.class);

            ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
            transactionalAction.runWithTransactional(() -> {
                if (threadManager.size() == 0) {
                    throw new StatusRelationshipErrorException();
                }

                ThreadObject thread = threadManager.getCurrent();

                if (!thread.getId().equals(userContext.getThreadId())) {
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
