package indi.sly.system.services.auxiliary.prototypes.processors;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.services.auxiliary.lang.UserContextProcessorCreateFunction;
import indi.sly.system.services.auxiliary.prototypes.wrappers.AuxiliaryProcessorMediator;
import indi.sly.system.services.auxiliary.values.UserContentDefinition;
import indi.sly.system.services.auxiliary.values.UserContentRequestRawDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserContextCreateContent extends AUserContextCreateResolver {
    public UserContextCreateContent() {
        this.create = (userContext, userRequest) -> {
            UserContentRequestRawDefinition contentRequestRaw;
            try {
                contentRequestRaw = ObjectUtil.transferFromString(
                        UserContentRequestRawDefinition.class, userRequest.getOrDefault("Content", null));
            } catch (RuntimeException ignored) {
                throw new StatusUnreadableException();
            }

            if (ValueUtil.isAnyNullOrEmpty(contentRequestRaw.getJob(), contentRequestRaw.getMethod())) {
                throw new ConditionParametersException();
            }
            for (Map.Entry<String, String> pair : contentRequestRaw.getRequest().entrySet()) {
                if (ValueUtil.isAnyNullOrEmpty(pair.getKey())) {
                    throw new ConditionParametersException();
                }
            }

            UserContentDefinition content = userContext.getContent();
            content.setJob(contentRequestRaw.getJob());
            content.setMethod(contentRequestRaw.getMethod());
            content.getRequest().putAll(contentRequestRaw.getRequest());

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
        return 1;
    }
}
