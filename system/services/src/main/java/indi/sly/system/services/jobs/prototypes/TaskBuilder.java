package indi.sly.system.services.jobs.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.*;
import indi.sly.system.kernel.core.prototypes.ABuilder;
import indi.sly.system.services.core.environment.values.ServiceKernelExtensionSpace;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.values.TaskAttributeType;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskBuilder extends ABuilder {
    @SuppressWarnings("NullAway.Init")
    protected JobFactory factory;

    public void create(String name, long attribute, @Nullable UUID processId, ATaskInitializer initializer) {
        if (StringUtil.isNameIllegal(name) || ObjectUtil.isNull(initializer)) {
            throw new ConditionParametersException();
        }

        ServiceKernelExtensionSpace serviceSpace = (ServiceKernelExtensionSpace) this.coreManager.getKernelSpace().getServiceSpace();

        if (serviceSpace.getNamedTaskIds().containsKey(name)) {
            throw new StatusAlreadyExistedException();
        }

        TaskDefinition task = new TaskDefinition();

        task.setId(UUIDUtil.createRandom());
        task.setAttribute(attribute);
        task.setName(name);
        if (LogicalUtil.isAllExist(task.getAttribute(), TaskAttributeType.HAS_PROCESS)
                && !ValueUtil.isAnyNullOrEmpty(processId)) {
            task.setProcessId(processId);
        }
        task.setInitializer(initializer);

        serviceSpace.getTasks().put(task.getId(), task);
        serviceSpace.getNamedTaskIds().put(task.getName(), task.getId());
    }

    public void delete(String name) {
        if (ValueUtil.isAnyNullOrEmpty(name)) {
            throw new ConditionParametersException();
        }

        ServiceKernelExtensionSpace serviceSpace = (ServiceKernelExtensionSpace) this.coreManager.getKernelSpace().getServiceSpace();

        UUID taskId = serviceSpace.getNamedTaskIds().get(name);

        if (ValueUtil.isAnyNullOrEmpty(taskId)) {
            throw new StatusNotExistedException();
        }

        TaskDefinition task = serviceSpace.getTasks().get(taskId);

        serviceSpace.getTasks().remove(task.getId());
        serviceSpace.getNamedTaskIds().remove(task.getName());
    }
}
