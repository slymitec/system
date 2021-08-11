package indi.sly.system.services.job;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.AService;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.services.job.prototypes.JobBuilder;
import indi.sly.system.services.job.prototypes.JobFactory;
import indi.sly.system.services.job.prototypes.JobObject;
import indi.sly.system.services.job.prototypes.JobRepositoryObject;
import indi.sly.system.services.job.prototypes.processors.AJobInitializer;
import indi.sly.system.services.job.values.JobDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobService extends AService {
    @Override
    public void startup(long startup) {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void check() {
    }

    protected JobFactory factory;

    public JobObject get(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        JobRepositoryObject jobRepository =
                this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL, JobRepositoryObject.class);

        JobDefinition job = jobRepository.getJobs().getOrDefault(id, null);

        if (ObjectUtil.isAnyNull(job)) {
            throw new StatusNotExistedException();
        }

        return this.factory.build(job);
    }

    public JobObject get(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        JobRepositoryObject jobRepository =
                this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL, JobRepositoryObject.class);

        UUID jobID = jobRepository.getJobIDs().getOrDefault(name, null);

        if (ValueUtil.isAnyNullOrEmpty(jobID)) {
            throw new StatusNotExistedException();
        }

        JobDefinition job = jobRepository.getJobs().getOrDefault(jobID, null);

        if (ObjectUtil.isAnyNull(job)) {
            throw new StatusNotExistedException();
        }

        return this.factory.build(job);
    }

    public JobObject create(String name, long attribute, UUID processID, AJobInitializer initializer) {
        JobBuilder jobBuilder = this.factory.createJob();

        return jobBuilder.create(name, attribute, processID, initializer);
    }

    public synchronized void delete(UUID id) {
        JobBuilder jobBuilder = this.factory.createJob();

        jobBuilder.delete(id);
    }
}
