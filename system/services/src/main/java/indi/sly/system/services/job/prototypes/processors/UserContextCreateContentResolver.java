package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.services.job.lang.UserContextProcessorCreateFunction;
import indi.sly.system.services.job.prototypes.wrappers.UserContextProcessorMediator;
import indi.sly.system.services.job.values.UserContentRequestDefinition;
import indi.sly.system.services.job.values.UserContextRequestContentRawDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextCreateContentResolver extends AUserContextCreateResolver {
    public UserContextCreateContentResolver() {
        this.create = (userContext, userContextRequestRaw) -> {
            UserContextRequestContentRawDefinition contentRaw = userContextRequestRaw.getContent();

            if (ValueUtil.isAnyNullOrEmpty(contentRaw.getTask(), contentRaw.getMethod())) {
                throw new ConditionParametersException();
            }
            for (String key : contentRaw.getRequest().keySet()) {
                if (ValueUtil.isAnyNullOrEmpty(key)) {
                    throw new ConditionParametersException();
                }
            }

            UserContentRequestDefinition userContentRequest = userContext.getContent().getRequest();
            userContentRequest.setTask(contentRaw.getTask());
            userContentRequest.setMethod(contentRaw.getMethod());
            userContentRequest.getRequest().putAll(contentRaw.getRequest());

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
