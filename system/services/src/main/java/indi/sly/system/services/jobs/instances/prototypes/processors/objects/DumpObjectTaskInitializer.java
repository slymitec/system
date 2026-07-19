package indi.sly.system.services.jobs.instances.prototypes.processors.objects;

import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.DumpObject;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.instances.prototypes.processors.ATaskInitializer;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.TaskDefinition;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DumpObjectTaskInitializer extends ATaskInitializer {
    public DumpObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(ObjectManager.class).getFactory().rebuildInfo(handle);

        this.register("getDate", this::getDate, TransactionType.INDEPENDENCE);
        this.register("getProcessId", this::getProcessId, TransactionType.INDEPENDENCE);
        this.register("getAccountId", this::getAccountId, TransactionType.INDEPENDENCE);
        this.register("getPath", this::getPath, TransactionType.INDEPENDENCE);
        this.register("getInfoOpen", this::getInfoOpen, TransactionType.INDEPENDENCE);
        this.register("getSecurityDescriptorSummary", this::getSecurityDescriptorSummary, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getDate(TaskRunConsumer run, TaskContentObject content) {
        DumpObject dump = content.getCacheableObject();

        content.setResult(dump.getDate());
    }

    private void getProcessId(TaskRunConsumer run, TaskContentObject content) {
        DumpObject dump = content.getCacheableObject();

        content.setResult(dump.getProcessId());
    }

    private void getAccountId(TaskRunConsumer run, TaskContentObject content) {
        DumpObject dump = content.getCacheableObject();

        content.setResult(dump.getAccountId());
    }

    private void getPath(TaskRunConsumer run, TaskContentObject content) {
        DumpObject dump = content.getCacheableObject();

        content.setResult(dump.getPath());
    }

    private void getInfoOpen(TaskRunConsumer run, TaskContentObject content) {
        DumpObject dump = content.getCacheableObject();

        content.setResult(dump.getInfoOpen());
    }

    private void getSecurityDescriptorSummary(TaskRunConsumer run, TaskContentObject content) {
        DumpObject dump = content.getCacheableObject();

        content.setResult(dump.getSecurityDescriptorSummary());
    }
}
