package indi.sly.system.services.jobs.instances.prototypes.processors.processes;

import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessStatusObject;
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
public class ProcessStatusObjectTaskInitializer extends ATaskInitializer {
    public ProcessStatusObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(ProcessManager.class).getFactory().rebuildProcessStatus(handle);

        this.register("get", this::get, TransactionType.INDEPENDENCE);
        this.register("run", this::run, TransactionType.INDEPENDENCE);
        this.register("interrupt", this::interrupt, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void get(TaskRunConsumer run, TaskContentObject content) {
        ProcessStatusObject ProcessStatus = content.getCacheableObject();

        content.setResult(ProcessStatus.get());
    }

    private void run(TaskRunConsumer run, TaskContentObject content) {
        ProcessStatusObject ProcessStatus = content.getCacheableObject();

        ProcessStatus.run();
    }

    private void interrupt(TaskRunConsumer run, TaskContentObject content) {
        ProcessStatusObject ProcessStatus = content.getCacheableObject();

        ProcessStatus.interrupt();
    }
}