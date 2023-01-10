package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.kernel.processes.prototypes.ThreadStatusObject;
import indi.sly.system.services.job.lang.TaskProcessorFinishConsumer;
import indi.sly.system.services.job.lang.TaskProcessorStartFunction;
import indi.sly.system.services.job.prototypes.wrappers.TaskProcessorMediator;
import indi.sly.system.services.job.values.TaskAttributeType;
import indi.sly.system.services.job.values.TaskDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskProcessAndThreadResolver extends ATaskResolver {
    public TaskProcessAndThreadResolver() {
        this.start = (job, status) -> {
            if (LogicalUtil.isAllExist(job.getAttribute(), TaskAttributeType.HAS_PROCESS)
                    && !ValueUtil.isAnyNullOrEmpty(job.getProcessID())) {
                ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
                ThreadObject thread = threadManager.create(job.getProcessID());

                ThreadStatusObject threadStatus = thread.getStatus();
                threadStatus.running();
            }
        };

        this.finish = (job, status) -> {
            if (LogicalUtil.isAllExist(job.getAttribute(), TaskAttributeType.HAS_PROCESS)
                    && !ValueUtil.isAnyNullOrEmpty(job.getProcessID())) {
                ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
                ThreadObject thread = threadManager.getCurrent();

                ThreadStatusObject threadStatus = thread.getStatus();
                threadStatus.die();
                threadManager.end();
            }
        };
    }

    @Override
    public int order() {
        return 1;
    }

    private final TaskProcessorStartFunction start;
    private final TaskProcessorFinishConsumer finish;

    @Override
    public void resolve(TaskDefinition task, TaskProcessorMediator processorMediator) {
        if (!ValueUtil.isAnyNullOrEmpty(task.getProcessID())) {
            processorMediator.getStarts().add(this.start);
            processorMediator.getFinishes().add(this.finish);
        }
    }
}
