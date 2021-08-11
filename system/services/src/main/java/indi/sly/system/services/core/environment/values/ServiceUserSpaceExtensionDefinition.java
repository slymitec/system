package indi.sly.system.services.core.environment.values;

import indi.sly.system.kernel.core.enviroment.values.AUserSpaceExtensionDefinition;
import indi.sly.system.services.job.values.JobPointerDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceUserSpaceExtensionDefinition extends AUserSpaceExtensionDefinition<ServiceUserSpaceExtensionDefinition> {
    public ServiceUserSpaceExtensionDefinition() {
        this.jobPointers = new ConcurrentHashMap<>();
    }

    private final Map<UUID, JobPointerDefinition> jobPointers;

    public Map<UUID, JobPointerDefinition> getJobPointers() {
        return this.jobPointers;
    }
}
