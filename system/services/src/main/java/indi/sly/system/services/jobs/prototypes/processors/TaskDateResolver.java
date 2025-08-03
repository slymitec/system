package indi.sly.system.services.jobs.prototypes.processors;

import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.services.jobs.lang.TaskProcessorContentFunction;
import indi.sly.system.services.jobs.lang.TaskProcessorFinishConsumer;
import indi.sly.system.services.jobs.lang.TaskProcessorRunConsumer;
import indi.sly.system.services.jobs.lang.TaskProcessorStartConsumer;
import indi.sly.system.services.jobs.prototypes.wrappers.TaskProcessorMediator;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Map;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskDateResolver extends ATaskResolver {
    public TaskDateResolver() {
        this.start = (task, status) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            Map<Long, Long> date = status.getDate();
            assert date != null;
            date.put(DateTimeType.CREATE, nowDateTime);
        };

        this.finish = (task, status) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            Map<Long, Long> date = status.getDate();
            assert date != null;
            date.put(DateTimeType.ACCESS, nowDateTime);
        };

        this.run = (task, status, name, run, content) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            Map<Long, Long> date = status.getDate();
            assert date != null;
            date.put(DateTimeType.ACCESS, nowDateTime);
        };

        this.content = (task, status, threadContext) -> {
            DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

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
    private final TaskProcessorFinishConsumer finish;
    private final TaskProcessorRunConsumer run;
    private final TaskProcessorContentFunction content;

    @Override
    public void resolve(TaskDefinition task, TaskProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
        processorMediator.getFinishes().add(this.finish);
        processorMediator.getRuns().add(this.run);
        processorMediator.getContents().add(this.content);
    }
}
