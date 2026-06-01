package indi.sly.system.services.jobs.instances.prototypes.processors.processes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoTableObject;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.HandleContextDefinition;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessInfoTableObjectTaskInitializer extends ATaskInitializer {
    public ProcessInfoTableObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(ProcessManager.class).getFactory().rebuildProcessInfoTable(handle);

        this.register("list", this::list, TransactionType.INDEPENDENCE);
        this.register("containByIndex", this::containByIndex, TransactionType.INDEPENDENCE);
        this.register("containById", this::containById, TransactionType.INDEPENDENCE);
        this.register("getByIndex", this::getByIndex, TransactionType.INDEPENDENCE);
        this.register("getById", this::getById, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void list(TaskRunConsumer run, TaskContentObject content) {
        ProcessInfoTableObject processInfoTable = content.getCacheableObject();

        content.setResult(processInfoTable.list());
    }

    private void containByIndex(TaskRunConsumer run, TaskContentObject content) {
        ProcessInfoTableObject processInfoTable = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID index = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        content.setResult(processInfoTable.containByIndex(index));
    }

    private void containById(TaskRunConsumer run, TaskContentObject content) {
        ProcessInfoTableObject processInfoTable = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID id = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        content.setResult(processInfoTable.containById(id));
    }

    private void getByIndex(TaskRunConsumer run, TaskContentObject content) {
        ProcessInfoTableObject processInfoTable = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID index = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        ProcessInfoEntryObject ProcessInfoEntry = processInfoTable.getByIndex(index);

        UUID handle = ProcessInfoEntry.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(ProcessInfoEntry.getClass()), handle);

        content.setResult(handleContext);
    }

    private void getById(TaskRunConsumer run, TaskContentObject content) {
        ProcessInfoTableObject processInfoTable = content.getCacheableObject();

        List<String> parameters = content.getParameters();

        if (parameters.isEmpty()) {
            throw new ConditionParametersException();
        }

        UUID id = ObjectUtil.transferFromString(UUID.class, parameters.getFirst());

        ProcessInfoEntryObject ProcessInfoEntry = processInfoTable.getById(id);

        UUID handle = ProcessInfoEntry.cache();

        HandleContextDefinition handleContext = new HandleContextDefinition(ClassUtil.getSimpleName(ProcessInfoEntry.getClass()), handle);

        content.setResult(handleContext);
    }
}