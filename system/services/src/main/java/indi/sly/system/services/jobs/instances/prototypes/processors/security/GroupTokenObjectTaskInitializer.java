package indi.sly.system.services.jobs.instances.prototypes.processors.security;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountTokenObject;
import indi.sly.system.kernel.security.prototypes.GroupTokenObject;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GroupTokenObjectTaskInitializer extends ATaskInitializer {
    public GroupTokenObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(UserManager.class).getFactory().rebuildGroupToken(handle);

        this.register("getPrivileges", this::getPrivileges, TransactionType.INDEPENDENCE);
        this.register("setPrivileges", this::setPrivileges, TransactionType.INDEPENDENCE);
        this.register("getLimits", this::getLimits, TransactionType.INDEPENDENCE);
        this.register("setLimits", this::setLimits, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getPrivileges(TaskRunConsumer run, TaskContentObject content) {
        GroupTokenObject groupToken = content.getCacheableObject();

        content.setResult(groupToken.getPrivileges());
    }

    private void setPrivileges(TaskRunConsumer run, TaskContentObject content) {
        GroupTokenObject groupToken = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        long privileges = ObjectUtil.transferFromString(Long.class, parameters.getFirst());

        groupToken.setPrivileges(privileges);
    }

    private void getLimits(TaskRunConsumer run, TaskContentObject content) {
        GroupTokenObject groupToken = content.getCacheableObject();

        content.setResult(groupToken.getLimits());
    }

    private void setLimits(TaskRunConsumer run, TaskContentObject content) {
        GroupTokenObject groupToken = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        Map<Long, Integer> limits = ObjectUtil.transferMapFromString(Long.class, Integer.class, parameters.getFirst());

        groupToken.setLimits(limits);
    }
}