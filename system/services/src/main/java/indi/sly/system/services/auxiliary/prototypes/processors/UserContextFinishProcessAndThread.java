package indi.sly.system.services.auxiliary.prototypes.processors;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.services.auxiliary.lang.UserContextProcessorFinishFunction;
import indi.sly.system.services.auxiliary.prototypes.wrappers.AuxiliaryProcessorMediator;
import indi.sly.system.services.core.prototypes.TransactionalActionObject;
import indi.sly.system.services.core.values.TransactionType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextFinishProcessAndThread extends AUserContextFinishResolver {
    public UserContextFinishProcessAndThread() {
        this.finish = (userContext) -> {
            TransactionalActionObject transactionalAction = this.factoryManager.create(TransactionalActionObject.class);

            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
            transactionalAction.run(TransactionType.INDEPENDENCE, () -> {
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
    public void resolve(AuxiliaryProcessorMediator processorMediator) {
        processorMediator.getFinishes().add(this.finish);
    }

    @Override
    public int order() {
        return 0;
    }
}
