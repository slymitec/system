package indi.sly.system.services.job;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.AService;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.services.core.environment.values.ServiceKernelSpaceExtensionDefinition;
import indi.sly.system.services.job.prototypes.*;
import indi.sly.system.services.job.prototypes.processors.AJobInitializer;
import indi.sly.system.services.job.values.JobDefinition;
import indi.sly.system.services.job.values.JobPointerDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobService extends AService {
    @Override
    public void startup(long startup) {
        if (startup == StartupType.STEP_INIT) {
            this.factoryManager.getKernelSpace().setServiceSpace(new ServiceKernelSpaceExtensionDefinition());
        } else if (startup == StartupType.STEP_SERVICE) {
        }
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void check() {
    }

    protected JobFactory factory;

    public JobObject getJob(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        ServiceKernelSpaceExtensionDefinition serviceSpace = (ServiceKernelSpaceExtensionDefinition) this.factoryManager.getKernelSpace().getServiceSpace();

        JobDefinition job = serviceSpace.getJobs().getOrDefault(id, null);

        if (ObjectUtil.isAnyNull(job)) {
            throw new StatusNotExistedException();
        }

        return this.factory.build(job);
    }

    public JobObject getJob(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        ServiceKernelSpaceExtensionDefinition serviceSpace = (ServiceKernelSpaceExtensionDefinition) this.factoryManager.getKernelSpace().getServiceSpace();

        UUID jobID = serviceSpace.getNamedJobIDs().getOrDefault(name, null);

        if (ValueUtil.isAnyNullOrEmpty(jobID)) {
            throw new StatusNotExistedException();
        }

        JobDefinition job = serviceSpace.getJobs().getOrDefault(jobID, null);

        if (ObjectUtil.isAnyNull(job)) {
            throw new StatusNotExistedException();
        }

        return this.factory.build(job);
    }

    public JobObject createJob(String name, long attribute, UUID processID, AJobInitializer initializer) {
        JobBuilder jobBuilder = this.factory.createJob();

        return jobBuilder.create(name, attribute, processID, initializer);
    }

    public void deleteJob(UUID id) {
        JobBuilder jobBuilder = this.factory.createJob();

        jobBuilder.delete(id);
    }


    public boolean containPointer(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        Map<UUID, JobPointerDefinition> jobPointers = this.factory.getJobPointers();

        return jobPointers.containsKey(id);
    }

    public void createPointer(UUID id) {
        JobPointerBuilder jobPointerBuilder = this.factory.createJobPointer();

        jobPointerBuilder.create(id);
    }

    public JobPointerObject getPointer(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        Map<UUID, JobPointerDefinition> jobPointers = this.factory.getJobPointers();

        JobPointerDefinition jobPointer = jobPointers.getOrDefault(id, null);

        if (ObjectUtil.isAnyNull(jobPointer)) {
            throw new StatusAlreadyExistedException();
        }

        return this.factory.build(jobPointer);
    }
}
