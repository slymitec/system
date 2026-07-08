package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.services.jobs.lang.UserContextProcessorCreateFunction;
import indi.sly.system.services.jobs.prototypes.mediators.UserContextProcessorMediator;
import indi.sly.system.services.jobs.values.UserContentRequestRecord;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextCreateContentResolver extends AResolver implements IUserContextCreateResolver {
    public UserContextCreateContentResolver() {
        this.create = (userContext, clientRequest) -> {
            UserContentRequestRecord userContentRequest = clientRequest.content();

            if (ObjectUtil.isAnyNull(userContentRequest) || ValueUtil.isAnyNullOrEmpty(userContentRequest.id(), userContentRequest.task(), userContentRequest.method())) {
                throw new ConditionParametersException();
            }

            userContext.getContent().setRequest(userContentRequest);

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
