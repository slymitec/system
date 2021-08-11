package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.*;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.ABuilder;
import indi.sly.system.services.job.prototypes.processors.AJobInitializer;
import indi.sly.system.services.job.values.JobAttributeType;
import indi.sly.system.services.job.values.JobDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobBuilder extends ABuilder {
    protected JobFactory factory;

    public JobObject create(String name, long attribute, UUID processID, AJobInitializer initializer) {
        if (StringUtil.isNameIllegal(name) || ObjectUtil.isAnyNull(initializer)) {
            throw new ConditionParametersException();
        }

        JobRepositoryObject jobRepository =
                this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL, JobRepositoryObject.class);

        if (jobRepository.getJobIDs().containsKey(name)) {
            throw new StatusAlreadyExistedException();
        }

        JobDefinition job = new JobDefinition();

        job.setID(UUIDUtil.createRandom());
        job.setAttribute(attribute);
        job.setName(name);
        if (LogicalUtil.isAnyExist(job.getAttribute(), JobAttributeType.HAS_PROCESS)
                && !ValueUtil.isAnyNullOrEmpty(processID)) {
            job.setProcessID(processID);
        }
        job.setInitializer(initializer);

        jobRepository.getJobs().put(job.getID(), job);
        jobRepository.getJobIDs().put(job.getName(), job.getID());

        return this.factory.build(job);
    }

    public void delete(UUID id) {
        if (ValueUtil.isAnyNullOrEmpty(id)) {
            throw new ConditionParametersException();
        }

        JobRepositoryObject jobRepository =
                this.factoryManager.getCorePrototypeRepository().get(SpaceType.KERNEL, JobRepositoryObject.class);

        if (!jobRepository.getJobs().containsKey(id)) {
            throw new StatusNotExistedException();
        }

        JobDefinition job = jobRepository.getJobs().getOrDefault(id, null);

        if (ObjectUtil.isAnyNull(job)) {
            throw new StatusNotExistedException();
        }

        jobRepository.getJobs().remove(job.getID());
        jobRepository.getJobIDs().remove(job.getName());
    }
}
