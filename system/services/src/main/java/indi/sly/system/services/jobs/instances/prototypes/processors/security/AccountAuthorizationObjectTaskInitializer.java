package indi.sly.system.services.jobs.instances.prototypes.processors.security;

import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.*;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountAuthorizationObjectTaskInitializer extends ATaskInitializer {
    public AccountAuthorizationObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(UserManager.class).getFactory().rebuildAccountAuthorization(handle);

        this.register("getDate", this::getDate, TransactionType.INDEPENDENCE);
        this.register("isLegal", this::isLegal, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getDate(TaskRunConsumer run, TaskContentObject content) {
        AccountAuthorizationObject accountAuthorization = content.getCacheableObject();

        content.setResult(accountAuthorization.getDate());
    }

    private void isLegal(TaskRunConsumer run, TaskContentObject content) {
        AccountAuthorizationObject accountAuthorization = content.getCacheableObject();

        content.setResult(accountAuthorization.isLegal());
    }
}