package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.services.jobs.lang.UserContextProcessorCreateFunction;
import indi.sly.system.services.jobs.prototypes.wrappers.UserContextProcessorMediator;
import indi.sly.system.services.jobs.values.UserContentRequestDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextCreateContentResolver extends AUserContextCreateResolver {
    public UserContextCreateContentResolver() {
        this.create = (userContext, clientRequest) -> {
            UserContentRequestDefinition userContentRequestRaw = clientRequest.getContent();

            if (ObjectUtil.isAnyNull(userContentRequestRaw.getId()) || ValueUtil.isAnyNullOrEmpty(userContentRequestRaw.getTask(), userContentRequestRaw.getMethod())) {
                throw new ConditionParametersException();
            }

            UserContentRequestDefinition userContentRequest = userContext.getContent().getRequest();
            userContentRequest.setId(userContentRequestRaw.getId());
            userContentRequest.setTask(userContentRequestRaw.getTask());
            userContentRequest.setMethod(userContentRequestRaw.getMethod());
            userContentRequest.getParameters().addAll(userContentRequestRaw.getParameters());

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
