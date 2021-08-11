package indi.sly.system.services.job.prototypes;

import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.services.job.values.JobDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobRepositoryObject extends AObject {
    public JobRepositoryObject() {
        this.jobs = new ConcurrentHashMap<>();
        this.jobIDs = new ConcurrentHashMap<>();
    }

    private final Map<UUID, JobDefinition> jobs;
    private final Map<String, UUID> jobIDs;

    public Map<UUID, JobDefinition> getJobs() {
        return this.jobs;
    }

    public Map<String, UUID> getJobIDs() {
        return this.jobIDs;
    }
}
