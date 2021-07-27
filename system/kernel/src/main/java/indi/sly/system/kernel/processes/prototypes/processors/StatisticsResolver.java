package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.lang.ReadProcessStatusFunction;
import indi.sly.system.kernel.processes.lang.WriteProcessStatusConsumer;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StatisticsResolver extends APrototype implements IProcessResolver {
    private final ReadProcessStatusFunction readProcessStatus;
    private final WriteProcessStatusConsumer writeProcessStatus;

    public StatisticsResolver() {
        this.readProcessStatus = (status, process) -> status;
        this.writeProcessStatus = (process, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessStatisticsObject processStatistics = processManager.get(process.getID()).getStatistics();
            processStatistics.addStatusCumulation(1);
        };
    }

    @Override
    public void resolve(ProcessEntity process, ProcessProcessorMediator processorMediator) {
        processorMediator.getReadProcessStatuses().add(readProcessStatus);
        processorMediator.getWriteProcessStatuses().add(writeProcessStatus);
    }
}
