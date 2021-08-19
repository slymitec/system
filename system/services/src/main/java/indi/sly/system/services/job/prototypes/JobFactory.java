package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.services.job.prototypes.processors.*;
import indi.sly.system.services.job.prototypes.wrappers.JobProcessorMediator;
import indi.sly.system.services.job.values.JobDefinition;
import indi.sly.system.services.job.values.JobStatusDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobFactory extends AFactory {
    protected List<AJobResolver> jobResolvers;

    @Override
    public void init() {
        this.jobResolvers = new CopyOnWriteArrayList<>();

        this.jobResolvers.add(this.factoryManager.create(JobCheckConditionResolver.class));
        this.jobResolvers.add(this.factoryManager.create(JobContentResolver.class));
        this.jobResolvers.add(this.factoryManager.create(JobInitializerResolver.class));
        this.jobResolvers.add(this.factoryManager.create(JobProcessAndThreadResolver.class));
        this.jobResolvers.add(this.factoryManager.create(JobStatusRuntimeResolver.class));

        Collections.sort(this.jobResolvers);
    }

    private JobObject build(JobProcessorMediator processorMediator, Provider<JobDefinition> funcRead,
                            Consumer1<JobDefinition> funcWrite) {
        JobObject job = this.factoryManager.create(JobObject.class);

        job.setSource(funcRead, funcWrite);
        job.processorMediator = processorMediator;
        job.status = new JobStatusDefinition();

        return job;
    }

    public JobObject build(JobDefinition job) {
        if (ObjectUtil.isAnyNull(job)) {
            throw new ConditionParametersException();
        }

        JobProcessorMediator processorMediator = this.factoryManager.create(JobProcessorMediator.class);
        for (AJobResolver resolver : this.jobResolvers) {
            resolver.resolve(job, processorMediator);
        }

        return this.build(processorMediator, () -> job, (source) -> {
        });
    }

    public JobBuilder createJob() {
        JobBuilder jobBuilder = this.factoryManager.create(JobBuilder.class);

        jobBuilder.factory = this;

        return jobBuilder;
    }
}
