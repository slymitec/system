package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.services.job.lang.JobProcessorContentFunction;
import indi.sly.system.services.job.lang.JobProcessorFinishConsumer;
import indi.sly.system.services.job.lang.JobProcessorRunConsumer;
import indi.sly.system.services.job.lang.JobProcessorStartFunction;
import indi.sly.system.services.job.prototypes.wrappers.JobProcessorMediator;
import indi.sly.system.services.job.values.JobDefinition;
import indi.sly.system.services.job.values.JobStatusRuntimeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobCheckConditionResolver extends AJobResolver {
    public JobCheckConditionResolver() {
        this.start = (job, status) -> {
            if (status.getRuntime() != JobStatusRuntimeType.INITIALIZATION) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.finish = (job, status) -> {
            if (status.getRuntime() != JobStatusRuntimeType.RUNNING) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.run = (job, status, name, run, content) -> {
            if (status.getRuntime() != JobStatusRuntimeType.RUNNING) {
                throw new StatusRelationshipErrorException();
            }

            if (content.isException()) {
                throw new StatusNotReadyException();
            }
        };

        this.content = (job, status, threadRun) -> {
            if (status.getRuntime() != JobStatusRuntimeType.RUNNING) {
                throw new StatusRelationshipErrorException();
            }

            return threadRun;
        };
    }

    @Override
    public int order() {
        return 0;
    }

    private final JobProcessorStartFunction start;
    private final JobProcessorFinishConsumer finish;
    private final JobProcessorRunConsumer run;
    private final JobProcessorContentFunction content;

    @Override
    public void resolve(JobDefinition job, JobProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
        processorMediator.getFinishes().add(this.finish);
        processorMediator.getRuns().add(this.run);
        processorMediator.getContents().add(this.content);
    }
}
