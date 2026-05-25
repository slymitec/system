package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
import indi.sly.system.services.jobs.lang.UserContextProcessorCreateFunction;
import indi.sly.system.services.jobs.prototypes.mediators.UserContextProcessorMediator;
import indi.sly.system.services.jobs.values.ClientRequestProcessIdDefinition;
import indi.sly.system.services.jobs.values.ClientRequestType;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextCreateCheckClientProcessIdResolver extends AResolver implements IUserContextCreateResolver {
    public UserContextCreateCheckClientProcessIdResolver() {
        this.create = (userContext, userContextRequest) -> {
            ClientRequestProcessIdDefinition userContextRequestProcessId = userContextRequest.getProcessId();

            UUID processId = userContextRequestProcessId.getId();

            if (ValueUtil.isAnyNullOrEmpty(processId)) {
                throw new ConditionRefuseException();
            }

            long clientType = userContextRequestProcessId.getType();
            if (LogicalUtil.isAnyEqual(clientType, ClientRequestType.CLIENT)) {
                // Check
            } else if (LogicalUtil.isAnyEqual(clientType, ClientRequestType.APPLICATION)) {
                // Check
            } else {
                throw new ConditionRefuseException();
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
        return 0;
    }
}
