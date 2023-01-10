package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.*;
import indi.sly.system.kernel.core.prototypes.ABuilder;
import indi.sly.system.services.core.environment.values.ServiceKernelSpaceExtensionDefinition;
import indi.sly.system.services.job.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.job.values.TaskAttributeType;
import indi.sly.system.services.job.values.TaskDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskBuilder extends ABuilder {
    protected JobFactory factory;

    public void create(String name, long attribute, UUID processID, ATaskInitializer initializer) {
        if (StringUtil.isNameIllegal(name) || ObjectUtil.isAnyNull(initializer)) {
            throw new ConditionParametersException();
        }

        ServiceKernelSpaceExtensionDefinition serviceSpace = (ServiceKernelSpaceExtensionDefinition) this.factoryManager.getKernelSpace().getServiceSpace();

        if (serviceSpace.getNamedTaskIDs().containsKey(name)) {
            throw new StatusAlreadyExistedException();
        }

        TaskDefinition task = new TaskDefinition();

        task.setID(UUIDUtil.createRandom());
        task.setAttribute(attribute);
        task.setName(name);
        if (LogicalUtil.isAllExist(task.getAttribute(), TaskAttributeType.HAS_PROCESS)
                && !ValueUtil.isAnyNullOrEmpty(processID)) {
            task.setProcessID(processID);
        }
        task.setInitializer(initializer);

        serviceSpace.getTasks().put(task.getID(), task);
        serviceSpace.getNamedTaskIDs().put(task.getName(), task.getID());
    }

    public void delete(String name) {
        if (ValueUtil.isAnyNullOrEmpty(name)) {
            throw new ConditionParametersException();
        }

        ServiceKernelSpaceExtensionDefinition serviceSpace = (ServiceKernelSpaceExtensionDefinition) this.factoryManager.getKernelSpace().getServiceSpace();

        UUID taskID = serviceSpace.getNamedTaskIDs().getOrDefault(name, null);

        if (ValueUtil.isAnyNullOrEmpty(taskID)) {
            throw new StatusNotExistedException();
        }

        TaskDefinition task = serviceSpace.getTasks().getOrDefault(taskID, null);

        serviceSpace.getTasks().remove(task.getID());
        serviceSpace.getNamedTaskIDs().remove(task.getName());
    }
}
