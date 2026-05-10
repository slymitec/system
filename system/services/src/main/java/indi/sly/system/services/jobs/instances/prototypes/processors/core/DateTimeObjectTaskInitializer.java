package indi.sly.system.services.jobs.instances.prototypes.processors.core;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.CoreManager;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.systemversion.prototypes.SystemVersionObject;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.HandleContextDefinition;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DateTimeObjectTaskInitializer extends ATaskInitializer {
    public DateTimeObjectTaskInitializer() {
        this.register("getCurrent", this::getCurrent, TransactionType.WHATEVER);
        this.register("correct", this::correct, TransactionType.WHATEVER);
    }

    @Override
    public void start(TaskDefinition task) {

    }

    @Override
    public void finish(TaskDefinition task) {

    }

    private DateTimeObject getCacheableObject(TaskContentObject content) {
        List<String> parameters = content.getParameters();

        HandleContextDefinition handleContext = ObjectUtil.transferFromString(HandleContextDefinition.class, parameters.get(0));

        CoreManager coreManager = this.coreManager.getManager(CoreManager.class);
        return coreManager.getFactory().rebuildDateTime(handleContext.getHandle());
    }

    private void getCurrent(TaskRunConsumer run, TaskContentObject content) {
        DateTimeObject dateTimeObject = this.getCacheableObject(content);

        content.setResult(dateTimeObject.getCurrent());
    }

    private void correct(TaskRunConsumer run, TaskContentObject content) {
        DateTimeObject dateTimeObject = this.getCacheableObject(content);

        List<String> parameters = content.getParameters();

        long dateTime = ObjectUtil.transferFromString(Long.class, parameters.get(1));
        dateTimeObject.correct(dateTime);
    }
}
