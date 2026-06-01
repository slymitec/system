package indi.sly.system.services.jobs.instances.prototypes.processors.security;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.*;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.HandleContextDefinition;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AccountAuthorizationObjectTaskInitializer extends ATaskInitializer {
    public AccountAuthorizationObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(ObjectManager.class).getFactory().rebuildInfo(handle);

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