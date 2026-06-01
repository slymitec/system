package indi.sly.system.services.jobs.instances.prototypes.processors.security.instances;

import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.instances.prototypes.AuditContentObject;
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
public class AuditContentObjectTaskInitializer extends ATaskInitializer {
    public AuditContentObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(ObjectManager.class).getFactory().rebuildInfoContent(handle);

        this.register("getProcessId", this::getProcessId, TransactionType.INDEPENDENCE);
        this.register("getAccountId", this::getAccountId, TransactionType.INDEPENDENCE);
        this.register("getPath", this::getPath, TransactionType.INDEPENDENCE);
        this.register("getUserIds", this::getUserIds, TransactionType.INDEPENDENCE);
        this.register("getAudit", this::getAudit, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getProcessId(TaskRunConsumer run, TaskContentObject content) {
        AuditContentObject AuditContent = content.getCacheableObject();

        content.setResult(AuditContent.getProcessId());
    }

    private void getAccountId(TaskRunConsumer run, TaskContentObject content) {
        AuditContentObject AuditContent = content.getCacheableObject();

        content.setResult(AuditContent.getAccountId());
    }

    private void getPath(TaskRunConsumer run, TaskContentObject content) {
        AuditContentObject AuditContent = content.getCacheableObject();

        content.setResult(AuditContent.getPath());
    }

    private void getUserIds(TaskRunConsumer run, TaskContentObject content) {
        AuditContentObject AuditContent = content.getCacheableObject();

        content.setResult(AuditContent.getUserIds());
    }

    private void getAudit(TaskRunConsumer run, TaskContentObject content) {
        AuditContentObject AuditContent = content.getCacheableObject();

        content.setResult(AuditContent.getAudit());
    }
}
