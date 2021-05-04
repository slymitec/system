package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.lang.Consumer2;
import indi.sly.system.common.lang.Function2;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StatisticsResolver extends APrototype implements IProcessResolver {
    private final Function2<Long, Long, ProcessEntity> readProcessStatus;
    private final Consumer2<ProcessEntity, Long> writeProcessStatus;

    public StatisticsResolver() {
        this.readProcessStatus = (status, process) -> status;
        this.writeProcessStatus = (process, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessStatisticsObject processStatistics = processManager.getProcess(process.getID()).getStatistics();
            processStatistics.addStatusCumulation(1);
        };
    }

    @Override
    public void resolve(ProcessEntity process, ProcessProcessorMediator processorMediator) {
        processorMediator.getReadProcessStatuses().add(readProcessStatus);
        processorMediator.getWriteProcessStatuses().add(writeProcessStatus);
    }
}
