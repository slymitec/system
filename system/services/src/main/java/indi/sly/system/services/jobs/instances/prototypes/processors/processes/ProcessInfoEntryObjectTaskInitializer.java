package indi.sly.system.services.jobs.instances.prototypes.processors.processes;

import indi.sly.system.common.supports.ClassUtil;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoEntryObject;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.HandleContextRecord;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessInfoEntryObjectTaskInitializer extends ATaskInitializer {
    public ProcessInfoEntryObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(ProcessManager.class).getFactory().rebuildProcessInfoEntry(handle);

        this.register("getIndex", this::getIndex, TransactionType.INDEPENDENCE);
        this.register("getDate", this::getDate, TransactionType.INDEPENDENCE);
        this.register("getPath", this::getPath, TransactionType.INDEPENDENCE);
        this.register("getOpen", this::getOpen, TransactionType.INDEPENDENCE);
        this.register("getInfo", this::getInfo, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getIndex(TaskRunConsumer run, TaskContentObject content) {
        ProcessInfoEntryObject processInfoEntry = content.getCacheableObject();

        content.setResult(processInfoEntry.getIndex());
    }

    private void getDate(TaskRunConsumer run, TaskContentObject content) {
        ProcessInfoEntryObject processInfoEntry = content.getCacheableObject();

        content.setResult(processInfoEntry.getDate());
    }

    private void getPath(TaskRunConsumer run, TaskContentObject content) {
        ProcessInfoEntryObject processInfoEntry = content.getCacheableObject();

        content.setResult(processInfoEntry.getPath());
    }

    private void getOpen(TaskRunConsumer run, TaskContentObject content) {
        ProcessInfoEntryObject processInfoEntry = content.getCacheableObject();

        content.setResult(processInfoEntry.getOpen());
    }

    private void getInfo(TaskRunConsumer run, TaskContentObject content) {
        ProcessInfoEntryObject processInfoEntry = content.getCacheableObject();

        InfoObject info = processInfoEntry.getInfo();

        UUID handle = info.cache();

        HandleContextRecord handleContext = new HandleContextRecord(ClassUtil.getSimpleName(info.getClass()), handle);

        content.setResult(handleContext);
    }
}