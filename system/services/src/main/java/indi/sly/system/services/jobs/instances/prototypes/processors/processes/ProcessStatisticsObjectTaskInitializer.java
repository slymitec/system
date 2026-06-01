package indi.sly.system.services.jobs.instances.prototypes.processors.processes;

import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessStatisticsObject;
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
public class ProcessStatisticsObjectTaskInitializer extends ATaskInitializer {
    public ProcessStatisticsObjectTaskInitializer() {
        this.cacheableObjectFunction = (handle) -> this.coreManager.getManager(ProcessManager.class).getFactory().rebuildProcessStatistics(handle);

        this.register("getDate", this::getDate, TransactionType.INDEPENDENCE);
        this.register("getStatistics", this::getStatistics, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void getDate(TaskRunConsumer run, TaskContentObject content) {
        ProcessStatisticsObject processStatistics = content.getCacheableObject();

        content.setResult(processStatistics.getDate());
    }

    private void getStatistics(TaskRunConsumer run, TaskContentObject content) {
        ProcessStatisticsObject processStatistics = content.getCacheableObject();

        content.setResult(processStatistics.getStatistics());
    }
}