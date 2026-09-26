package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.common.values.DateTimeType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.services.jobs.lang.TaskProcessorContentFunction;
import indi.sly.system.services.jobs.lang.TaskProcessorEndConsumer;
import indi.sly.system.services.jobs.lang.TaskProcessorRunConsumer;
import indi.sly.system.services.jobs.lang.TaskProcessorStartConsumer;
import indi.sly.system.services.jobs.prototypes.mediators.TaskProcessorMediator;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskDateResolver extends AResolver implements ITaskResolver {
    public TaskDateResolver() {
        this.start = (task, status) -> {
            DateTimeObject dateTime = this.coreManager.getDateTime();
            long nowDateTime = dateTime.getCurrent();

            Map<Long, Long> date = status.getDate();
            assert date != null;
            date.put(DateTimeType.CREATE, nowDateTime);
        };

        this.end = (task, status) -> {
            DateTimeObject dateTime = this.coreManager.getDateTime();
            long nowDateTime = dateTime.getCurrent();

            Map<Long, Long> date = status.getDate();
            assert date != null;
            date.put(DateTimeType.ACCESS, nowDateTime);
        };

        this.run = (task, status, name, content) -> {
            DateTimeObject dateTime = this.coreManager.getDateTime();
            long nowDateTime = dateTime.getCurrent();

            Map<Long, Long> date = status.getDate();
            assert date != null;
            date.put(DateTimeType.ACCESS, nowDateTime);
        };

        this.content = (task, status, threadContext) -> {
            DateTimeObject dateTime = this.coreManager.getDateTime();
            long nowDateTime = dateTime.getCurrent();

            Map<Long, Long> date = status.getDate();
            assert date != null;
            date.put(DateTimeType.ACCESS, nowDateTime);

            return threadContext;
        };
    }

    @Override
    public int order() {
        return 3;
    }

    private final TaskProcessorStartConsumer start;
    private final TaskProcessorEndConsumer end;
    private final TaskProcessorRunConsumer run;
    private final TaskProcessorContentFunction content;

    @Override
    public void resolve(TaskDefinition task, TaskProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
        processorMediator.getEnds().add(this.end);
        processorMediator.getRuns().add(this.run);
        processorMediator.getContents().add(this.content);
    }
}
