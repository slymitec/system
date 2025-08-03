package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.services.jobs.lang.UserContextProcessorCreateFunction;
import indi.sly.system.services.jobs.prototypes.wrappers.UserContextProcessorMediator;
import indi.sly.system.services.jobs.values.UserContextRequestProcessIDDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextCreateProcessIDVerificationResolver extends AUserContextCreateResolver {
    public UserContextCreateProcessIDVerificationResolver() {
        this.create = (userContext, userContextRequest) -> {
            UserContextRequestProcessIDDefinition userContextRequestProcessID = userContextRequest.getProcessID();

            UUID processID = userContextRequestProcessID.getID();

            if (ValueUtil.isAnyNullOrEmpty(processID)) {
                throw new ConditionRefuseException();
            }

//            userContextRequestProcessID.getVerification()

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
