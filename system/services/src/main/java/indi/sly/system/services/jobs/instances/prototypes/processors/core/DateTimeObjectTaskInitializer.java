package indi.sly.system.services.jobs.instances.prototypes.processors.core;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DateTimeObjectTaskInitializer extends ATaskInitializer {
    public DateTimeObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getFactory().rebuildDateTime(handle);

        this.register("getCurrent", this::getCurrent, TransactionType.WHATEVER);
        this.register("correct", this::correct, TransactionType.WHATEVER);
    }

    @Override
    public void start(TaskDefinition task) {

    }

    @Override
    public void finish(TaskDefinition task) {

    }

    private void getCurrent(TaskRunConsumer run, TaskContentObject content) {
        DateTimeObject dateTime = content.getCacheableObject();

        content.setResult(dateTime.getCurrent());
    }

    private void correct(TaskRunConsumer run, TaskContentObject content) {
        DateTimeObject dateTimeObject = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        long dateTime = ObjectUtil.transferFromString(Long.class, parameters.get(1));
        dateTimeObject.correct(dateTime);
    }
}
