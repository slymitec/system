package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.prototypes.processors.IProcessResolver;
import indi.sly.system.services.job.prototypes.processors.IJobResolver;
import indi.sly.system.services.job.prototypes.wrappers.JobProcessorMediator;
import indi.sly.system.services.job.values.JobDefinition;
import indi.sly.system.services.job.values.JobStatusDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobFactory extends AFactory {
    protected List<IJobResolver> jobResolvers;

    @Override
    public void init() {
        this.jobResolvers = new CopyOnWriteArrayList<>();

        Set<APrototype> corePrototypes =
                this.factoryManager.getCorePrototypeRepository().getByImplementInterface(SpaceType.KERNEL, IProcessResolver.class);

        for (APrototype prototype : corePrototypes) {
            if (prototype instanceof IJobResolver) {
                this.jobResolvers.add((IJobResolver) prototype);
            }
        }

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
        for (IJobResolver resolver : this.jobResolvers) {
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
