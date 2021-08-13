package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.services.core.environment.values.ServiceUserSpaceExtensionDefinition;
import indi.sly.system.services.job.prototypes.processors.*;
import indi.sly.system.services.job.prototypes.wrappers.JobProcessorMediator;
import indi.sly.system.services.job.values.JobDefinition;
import indi.sly.system.services.job.values.JobPointerDefinition;
import indi.sly.system.services.job.values.JobStatusDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobFactory extends AFactory {
    protected List<IJobResolver> jobResolvers;

    @Override
    public void init() {
        this.jobResolvers = new CopyOnWriteArrayList<>();

        Set<AResolver> resolvers = new HashSet<>();
        resolvers.add(this.factoryManager.create(JobCheckConditionResolver.class));
        resolvers.add(this.factoryManager.create(JobContentResolver.class));
        resolvers.add(this.factoryManager.create(JobInitializerResolver.class));
        resolvers.add(this.factoryManager.create(JobPointerResolver.class));
        resolvers.add(this.factoryManager.create(JobProcessAndThreadResolver.class));
        resolvers.add(this.factoryManager.create(JobStatusRuntimeResolver.class));

        for (AResolver resolver : resolvers) {
            if (resolver instanceof IJobResolver) {
                this.jobResolvers.add((IJobResolver) resolver);
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

    private JobPointerObject build(Provider<JobPointerDefinition> funcRead,
                                   Consumer1<JobPointerDefinition> funcWrite) {
        JobPointerObject jobPointer = this.factoryManager.create(JobPointerObject.class);

        jobPointer.setSource(funcRead, funcWrite);

        return jobPointer;
    }

    public JobPointerObject build(JobPointerDefinition jobPointer) {
        if (ObjectUtil.isAnyNull(jobPointer)) {
            throw new ConditionParametersException();
        }

        return this.build(() -> jobPointer, (source) -> {
        });
    }

    public JobPointerBuilder createJobPointer() {
        JobPointerBuilder jobPointerBuilder = this.factoryManager.create(JobPointerBuilder.class);

        jobPointerBuilder.factory = this;

        return jobPointerBuilder;
    }

    public Map<UUID, JobPointerDefinition> getJobPointers() {
        KernelConfigurationDefinition configuration = this.factoryManager.getKernelSpace().getConfiguration();

        UserSpaceDefinition userSpace = this.factoryManager.getUserSpace();
        ServiceUserSpaceExtensionDefinition serviceUserSpaceExtension =
                (ServiceUserSpaceExtensionDefinition) userSpace.getServiceSpace();

        return serviceUserSpaceExtension.getJobPointers();
    }
}
