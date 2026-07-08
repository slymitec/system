package indi.sly.system.services.jobs.instances.prototypes.processors.core;

import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.systemversion.prototypes.SystemVersionObject;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.HandleContextRecord;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CoreManagerTaskInitializer extends ATaskInitializer {
    public CoreManagerTaskInitializer() {
        this.cacheableObjectFunction = (_) -> this.coreManager;

        this.register("getSystemVersion", this::getSystemVersion, TransactionType.WHATEVER);
        this.register("getDateTime", this::getDateTime, TransactionType.WHATEVER);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getSystemVersion(TaskRunConsumer run, TaskContentObject content) {
        SystemVersionObject systemVersion = this.coreManager.getSystemVersion();

        UUID handle = systemVersion.cache();

        HandleContextRecord handleContext = new HandleContextRecord(ClassUtil.getSimpleName(systemVersion.getClass()), handle);

        content.setResult(handleContext);
    }

    private void getDateTime(TaskRunConsumer run, TaskContentObject content) {
        DateTimeObject dateTime = this.coreManager.getDateTime();

        UUID handle = dateTime.cache();

        HandleContextRecord handleContext = new HandleContextRecord(ClassUtil.getSimpleName(dateTime.getClass()), handle);

        content.setResult(handleContext);
    }
}
