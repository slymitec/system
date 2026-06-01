package indi.sly.system.services.jobs.instances.prototypes.processors.security;

import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.DumpObject;
import indi.sly.system.kernel.security.prototypes.GroupObject;
import indi.sly.system.kernel.security.prototypes.GroupTokenObject;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.HandleContextDefinition;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GroupObjectTaskInitializer extends ATaskInitializer {
    public GroupObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(ObjectManager.class).getFactory().rebuildInfo(handle);

        this.register("getId", this::getId, TransactionType.INDEPENDENCE);
        this.register("getName", this::getName, TransactionType.INDEPENDENCE);
        this.register("getToken", this::getToken, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getId(TaskRunConsumer run, TaskContentObject content) {
        GroupObject group = content.getCacheableObject();

        content.setResult(group.getId());
    }

    private void getName(TaskRunConsumer run, TaskContentObject content) {
        GroupObject group = content.getCacheableObject();

        content.setResult(group.getName());
    }

    private void getToken(TaskRunConsumer run, TaskContentObject content) {
        GroupObject group = content.getCacheableObject();

        GroupTokenObject groupToken = group.getToken();

        UUID handle = groupToken.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(groupToken.getClass()), handle);

        content.setResult(handleContext);
    }

}