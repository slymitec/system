package indi.sly.system.services.core.environment.values;

import indi.sly.system.kernel.core.enviroment.containers.AKernelExtensionSpace;
import indi.sly.system.services.core.prototypes.TransactionalActionComponent;
import indi.sly.system.services.jobs.values.TaskDefinition;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceKernelExtensionSpace extends AKernelExtensionSpace {
    public ServiceKernelExtensionSpace() {
        this.tasks = new ConcurrentHashMap<>();
        this.namedTaskIds = new ConcurrentHashMap<>();
    }

    private TransactionalActionComponent transactionalAction;
    private final Map<UUID, TaskDefinition> tasks;
    private final Map<String, UUID> namedTaskIds;

    public TransactionalActionComponent getTransactionalAction() {
        return this.transactionalAction;
    }

    public void setTransactionalAction(TransactionalActionComponent transactionalAction) {
        this.transactionalAction = transactionalAction;
    }

    public Map<UUID, TaskDefinition> getTasks() {
        return this.tasks;
    }

    public Map<String, UUID> getNamedTaskIds() {
        return this.namedTaskIds;
    }
}
