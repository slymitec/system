package indi.sly.system.services.jobs.instances.prototypes.processors.processes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessSessionObject;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessSessionObjectTaskInitializer extends ATaskInitializer {
    public ProcessSessionObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(ProcessManager.class).getFactory().rebuildProcessSession(handle);

        this.register("getId", this::getId, TransactionType.INDEPENDENCE);
        this.register("setId", this::setId, TransactionType.INDEPENDENCE);
        this.register("getType", this::getType, TransactionType.INDEPENDENCE);
        this.register("setType", this::setType, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getId(TaskRunConsumer run, TaskContentObject content) {
        ProcessSessionObject processSession = content.getCacheableObject();

        content.setResult(processSession.getId());
    }

    private void setId(TaskRunConsumer run, TaskContentObject content) {
        ProcessSessionObject processSession = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID id = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        processSession.setId(id);
    }

    private void getType(TaskRunConsumer run, TaskContentObject content) {
        ProcessSessionObject processSession = content.getCacheableObject();

        content.setResult(processSession.getType());
    }

    private void setType(TaskRunConsumer run, TaskContentObject content) {
        ProcessSessionObject processSession = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        long type = ObjectUtil.transferFromString(Long.class, parameters.getFirst());

        processSession.setType(type);
    }
}