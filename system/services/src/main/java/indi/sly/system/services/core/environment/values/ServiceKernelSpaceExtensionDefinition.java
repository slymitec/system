package indi.sly.system.services.core.environment.values;

import indi.sly.system.kernel.core.enviroment.values.AKernelSpaceExtensionDefinition;
import indi.sly.system.services.jobs.values.TaskDefinition;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceKernelSpaceExtensionDefinition extends AKernelSpaceExtensionDefinition<ServiceKernelSpaceExtensionDefinition> {
    public ServiceKernelSpaceExtensionDefinition() {
        this.tasks = new ConcurrentHashMap<>();
        this.namedTaskIDs = new ConcurrentHashMap<>();
    }

    private final Map<UUID, TaskDefinition> tasks;
    private final Map<String, UUID> namedTaskIDs;

    public Map<UUID, TaskDefinition> getTasks() {
        return this.tasks;
    }

    public Map<String, UUID> getNamedTaskIDs() {
        return this.namedTaskIDs;
    }
}
