package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.kernel.processes.prototypes.ThreadStatusObject;
import indi.sly.system.services.job.lang.JobProcessorFinishConsumer;
import indi.sly.system.services.job.lang.JobProcessorStartFunction;
import indi.sly.system.services.job.prototypes.wrappers.JobProcessorMediator;
import indi.sly.system.services.job.values.JobAttributeType;
import indi.sly.system.services.job.values.JobDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobProcessAndThreadResolver extends AResolver implements IJobResolver {
    public JobProcessAndThreadResolver() {
        this.start = (job, status) -> {
            if (LogicalUtil.isAllExist(job.getAttribute(), JobAttributeType.HAS_PROCESS)
                    && !ValueUtil.isAnyNullOrEmpty(job.getProcessID())) {
                ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
                ThreadObject thread = threadManager.create(job.getProcessID());

                ThreadStatusObject threadStatus = thread.getStatus();
                threadStatus.running();
            }
        };

        this.finish = (job, status) -> {
            if (LogicalUtil.isAllExist(job.getAttribute(), JobAttributeType.HAS_PROCESS)
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

    private final JobProcessorStartFunction start;
    private final JobProcessorFinishConsumer finish;

    @Override
    public void resolve(JobDefinition job, JobProcessorMediator processorMediator) {
        if (!ValueUtil.isAnyNullOrEmpty(job.getProcessID())) {
            processorMediator.getStarts().add(this.start);
            processorMediator.getFinishes().add(this.finish);
        }
    }
}
