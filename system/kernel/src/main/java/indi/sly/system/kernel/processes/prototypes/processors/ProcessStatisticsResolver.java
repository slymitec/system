package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.lang.ProcessProcessorReadStatusFunction;
import indi.sly.system.kernel.processes.lang.ProcessProcessorWriteStatusConsumer;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessStatisticsObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessStatisticsResolver extends AProcessResolver {
    private final ProcessProcessorReadStatusFunction readProcessStatus;
    private final ProcessProcessorWriteStatusConsumer writeProcessStatus;

    public ProcessStatisticsResolver() {
        this.readProcessStatus = (status, process) -> status;
        this.writeProcessStatus = (process, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject currentProcess = processManager.getCurrent();

            if (process.getID().equals(currentProcess.getID())) {
                ProcessStatisticsObject processStatistics = currentProcess.getStatistics();
                processStatistics.addStatusCumulation(1);
            }
        };
    }

    @Override
    public void resolve(ProcessEntity process, ProcessProcessorMediator processorMediator) {
        processorMediator.getReadProcessStatuses().add(readProcessStatus);
        processorMediator.getWriteProcessStatuses().add(writeProcessStatus);
    }
}
