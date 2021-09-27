package indi.sly.system.services.auxiliary.prototypes.processors;

import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.services.auxiliary.lang.UserContextProcessorCreateFunction;
import indi.sly.system.services.auxiliary.prototypes.wrappers.AuxiliaryProcessorMediator;
import indi.sly.system.services.auxiliary.values.UserContextProcessIDRawDefinition;
import indi.sly.system.services.core.prototypes.TransactionalActionObject;
import indi.sly.system.services.core.values.TransactionType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextCreateProcessAndThread extends AUserContextCreateResolver {
    public UserContextCreateProcessAndThread() {
        this.create = (userContext, userContextRequestRaw) -> {
            UserContextProcessIDRawDefinition processIDRaw;
            try {
                processIDRaw = ObjectUtil.transferFromString(
                        UserContextProcessIDRawDefinition.class, userContextRequestRaw.getValue().getOrDefault("Request", null));
            } catch (RuntimeException ignored) {
                throw new StatusUnreadableException();
            }

//            userContentProcessIDRaw.getVerification();

            UUID processID = processIDRaw.getProcessID();

            TransactionalActionObject transactionalAction = this.factoryManager.create(TransactionalActionObject.class);

            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
            ThreadObject thread = transactionalAction.run(TransactionType.INDEPENDENCE, () -> threadManager.create(processID));

            userContext.setThreadID(thread.getID());

            return userContext;
        };
    }

    private final UserContextProcessorCreateFunction create;

    @Override
    public void resolve(AuxiliaryProcessorMediator processorMediator) {
        processorMediator.getCreates().add(this.create);
    }

    @Override
    public int order() {
        return 2;
    }
}
