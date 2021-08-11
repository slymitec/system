package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.job.JobService;
import indi.sly.system.services.job.lang.JobProcessorStartFunction;
import indi.sly.system.services.job.prototypes.wrappers.JobProcessorMediator;
import indi.sly.system.services.job.values.JobDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobPointerResolver extends APrototype implements IJobResolver {
    public JobPointerResolver() {
        this.start = (job, status) -> {
            JobService jobService = this.factoryManager.getService(JobService.class);
            if (!jobService.containPointer(job.getID())) {
                jobService.createPointer(job.getID());
            }
        };
    }

    @Override
    public int order() {
        return 2;
    }

    private final JobProcessorStartFunction start;

    @Override
    public void resolve(JobDefinition job, JobProcessorMediator processorMediator) {
        if (!ValueUtil.isAnyNullOrEmpty(job.getProcessID())) {
            processorMediator.getStarts().add(this.start);
        }
    }
}
