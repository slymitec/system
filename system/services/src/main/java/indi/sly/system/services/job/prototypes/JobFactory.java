package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.enviroment.values.AUserSpaceExtensionDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.prototypes.processors.IProcessResolver;
import indi.sly.system.services.core.environment.values.ServiceUserSpaceExtensionDefinition;
import indi.sly.system.services.job.prototypes.processors.IJobResolver;
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
        AUserSpaceExtensionDefinition<?> aServiceExtensions =
                userSpace.getServiceExtensions().getOrDefault(configuration.CORE_ENVIRONMENT_USER_SPACE_EXTENSION_SERVICE, null);
        if (ObjectUtil.isAnyNull(aServiceExtensions) || !(aServiceExtensions instanceof ServiceUserSpaceExtensionDefinition)) {
            throw new StatusNotExistedException();
        }
        ServiceUserSpaceExtensionDefinition serviceUserSpaceExtension = (ServiceUserSpaceExtensionDefinition) aServiceExtensions;

        return serviceUserSpaceExtension.getJobPointers();
    }
}
