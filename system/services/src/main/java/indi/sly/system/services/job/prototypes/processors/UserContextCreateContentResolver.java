package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.services.job.lang.UserContextProcessorCreateFunction;
import indi.sly.system.services.job.prototypes.wrappers.UserContextProcessorMediator;
import indi.sly.system.services.job.values.UserContentRequestDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextCreateContentResolver extends AUserContextCreateResolver {
    public UserContextCreateContentResolver() {
        this.create = (userContext, userContextRequestRaw) -> {
            UserContentRequestDefinition userContentRequestRaw = userContextRequestRaw.getContent();

            if (ValueUtil.isAnyNullOrEmpty(userContentRequestRaw.getTask(), userContentRequestRaw.getMethod())) {
                throw new ConditionParametersException();
            }
            for (String key : userContentRequestRaw.getRequest().keySet()) {
                if (ValueUtil.isAnyNullOrEmpty(key)) {
                    throw new ConditionParametersException();
                }
            }

            UserContentRequestDefinition userContentRequest = userContext.getContent().getRequest();
            userContentRequest.setTask(userContentRequestRaw.getTask());
            userContentRequest.setMethod(userContentRequestRaw.getMethod());
            userContentRequest.getRequest().putAll(userContentRequestRaw.getRequest());

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
