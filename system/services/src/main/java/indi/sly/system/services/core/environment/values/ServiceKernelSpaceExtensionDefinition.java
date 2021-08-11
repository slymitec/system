package indi.sly.system.services.core.environment.values;

import indi.sly.system.kernel.core.enviroment.values.AKernelSpaceExtensionDefinition;
import indi.sly.system.services.job.values.JobDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceKernelSpaceExtensionDefinition extends AKernelSpaceExtensionDefinition<ServiceKernelSpaceExtensionDefinition> {
    public ServiceKernelSpaceExtensionDefinition() {
        this.jobs = new ConcurrentHashMap<>();
        this.namedJobIDs = new ConcurrentHashMap<>();
    }

    private final Map<UUID, JobDefinition> jobs;
    private final Map<String, UUID> namedJobIDs;

    public Map<UUID, JobDefinition> getJobs() {
        return this.jobs;
    }

    public Map<String, UUID> getNamedJobIDs() {
        return this.namedJobIDs;
    }
}
