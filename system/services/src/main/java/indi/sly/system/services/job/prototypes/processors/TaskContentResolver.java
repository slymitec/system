package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.services.job.lang.TaskProcessorContentFunction;
import indi.sly.system.services.job.prototypes.wrappers.TaskProcessorMediator;
import indi.sly.system.services.job.values.TaskDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskContentResolver extends ATaskResolver {
    public TaskContentResolver() {
        this.content = (job, status, threadRun) -> {
            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
            ThreadObject thread = threadManager.getCurrent();

            return thread.getContext();
        };
    }

    @Override
    public int order() {
        return 3;
    }

    private final TaskProcessorContentFunction content;

    @Override
    public void resolve(TaskDefinition task, TaskProcessorMediator processorMediator) {
        processorMediator.getContents().add(this.content);
    }
}
