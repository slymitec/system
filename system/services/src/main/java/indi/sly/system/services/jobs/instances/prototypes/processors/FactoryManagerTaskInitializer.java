package indi.sly.system.services.jobs.instances.prototypes.processors;

import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.SystemVersionObject;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.HandledObjectDefinition;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FactoryManagerTaskInitializer extends ATaskInitializer {
    public FactoryManagerTaskInitializer() {
        this.register("getDateTime", this::getDateTime, TransactionType.WHATEVER);
        this.register("getSystemVersion", this::getSystemVersion, TransactionType.WHATEVER);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getDateTime(TaskRunConsumer run, TaskContentObject content) {
        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);

        UUID handle = dateTime.cache(SpaceType.USER);

        HandledObjectDefinition handledObject = new HandledObjectDefinition();
        handledObject.setHandle(handle);
        handledObject.setType(dateTime.getClass());

        content.setResult(handledObject);
    }

    private void getSystemVersion(TaskRunConsumer run, TaskContentObject content) {
        SystemVersionObject systemVersion = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, SystemVersionObject.class);

        UUID handle = systemVersion.cache(SpaceType.USER);

        HandledObjectDefinition handledObject = new HandledObjectDefinition();
        handledObject.setHandle(handle);
        handledObject.setType(systemVersion.getClass());

        content.setResult(handledObject);
    }
}
