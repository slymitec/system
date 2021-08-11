package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.job.lang.JobProcessorFinishConsumer;
import indi.sly.system.services.job.lang.JobProcessorStartFunction;
import indi.sly.system.services.job.prototypes.wrappers.JobProcessorMediator;
import indi.sly.system.services.job.values.JobDefinition;
import indi.sly.system.services.job.values.JobStatusRuntimeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobStatusRuntimeResolver extends APrototype implements IJobResolver {
    public JobStatusRuntimeResolver() {
        this.start = (job, status) -> {
            status.setRuntime(JobStatusRuntimeType.RUNNING);
        };

        this.finish = (job, status) -> {
            status.setRuntime(JobStatusRuntimeType.FINISHED);
        };
    }

    @Override
    public int order() {
        return 3;
    }

    private final JobProcessorStartFunction start;
    private final JobProcessorFinishConsumer finish;

    @Override
    public void resolve(JobDefinition job, JobProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
        processorMediator.getFinishes().add(this.finish);
    }
}
