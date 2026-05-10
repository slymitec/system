package indi.sly.system.services.jobs.instances.prototypes.processors.core;

import indi.sly.system.kernel.core.systemversion.prototypes.SystemVersionObject;
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
public class SystemVersionObjectTaskInitializer extends ATaskInitializer {
    public SystemVersionObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getFactory().rebuildSystemVersion(handle);

        this.register("getSystemVersion", this::getSystemVersion, TransactionType.WHATEVER);
    }

    @Override
    public void start(TaskDefinition task) {

    }

    @Override
    public void finish(TaskDefinition task) {

    }

    private void getSystemVersion(TaskRunConsumer run, TaskContentObject content) {
        SystemVersionObject systemVersion = content.getCacheableObject();

        content.setResult(systemVersion.getSystemVersion());
    }
}
