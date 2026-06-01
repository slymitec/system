package indi.sly.system.services.jobs.instances.prototypes.processors.security;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountSessionsObject;
import indi.sly.system.kernel.security.prototypes.AccountTokenObject;
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
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountSessionsObjectTaskInitializer extends ATaskInitializer {
    public AccountSessionsObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(UserManager.class).getFactory().rebuildAccountSessions(handle);

        this.register("listSessions", this::listSessions, TransactionType.INDEPENDENCE);
        this.register("addSession", this::addSession, TransactionType.INDEPENDENCE);
        this.register("deleteSession", this::deleteSession, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void listSessions(TaskRunConsumer run, TaskContentObject content) {
        AccountSessionsObject accountSessions = content.getCacheableObject();

        content.setResult(accountSessions.listSessions());
    }

    private void addSession(TaskRunConsumer run, TaskContentObject content) {
        AccountSessionsObject accountSessions = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID sessionId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        accountSessions.addSession(sessionId);
    }

    private void deleteSession(TaskRunConsumer run, TaskContentObject content) {
        AccountSessionsObject accountSessions = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID sessionId = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        accountSessions.deleteSession(sessionId);
    }

}