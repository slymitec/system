package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.lang.ProcessProcessorReadProcessComponentFunction;
import indi.sly.system.kernel.processes.lang.ProcessProcessorReadProcessStatusFunction;
import indi.sly.system.kernel.processes.lang.ProcessProcessorWriteProcessComponentConsumer;
import indi.sly.system.kernel.processes.lang.ProcessProcessorWriteProcessStatusConsumer;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessMemberResolver extends APrototype implements IProcessResolver {
    private final ProcessProcessorReadProcessStatusFunction readProcessStatus;
    private final ProcessProcessorWriteProcessStatusConsumer writeProcessStatus;
    private final ProcessProcessorReadProcessComponentFunction readProcessCommunication;
    private final ProcessProcessorWriteProcessComponentConsumer writeProcessCommunication;
    private final ProcessProcessorReadProcessComponentFunction readProcessContext;
    private final ProcessProcessorWriteProcessComponentConsumer writeProcessContext;
    private final ProcessProcessorReadProcessComponentFunction readProcessHandleTable;
    private final ProcessProcessorWriteProcessComponentConsumer writeProcessHandleTable;
    private final ProcessProcessorReadProcessComponentFunction readProcessStatistics;
    private final ProcessProcessorWriteProcessComponentConsumer writeProcessStatistics;
    private final ProcessProcessorReadProcessComponentFunction readProcessToken;
    private final ProcessProcessorWriteProcessComponentConsumer writeProcessToken;

    public ProcessMemberResolver() {
        this.readProcessStatus = (status, process) -> process.getStatus();
        this.writeProcessStatus = ProcessEntity::setStatus;

        this.readProcessCommunication = (communication, process) -> process.getCommunication();
        this.writeProcessCommunication = ProcessEntity::setCommunication;

        this.readProcessContext = (context, process) -> process.getContext();
        this.writeProcessContext = ProcessEntity::setContext;

        this.readProcessHandleTable = (handleTable, process) -> process.getHandleTable();
        this.writeProcessHandleTable = ProcessEntity::setHandleTable;

        this.readProcessStatistics = (statistics, process) -> process.getStatistics();
        this.writeProcessStatistics = ProcessEntity::setStatistics;

        this.readProcessToken = (token, process) -> process.getToken();
        this.writeProcessToken = ProcessEntity::setToken;
    }

    @Override
    public void resolve(ProcessEntity process, ProcessProcessorMediator processorMediator) {
        processorMediator.getReadProcessStatuses().add(this.readProcessStatus);
        processorMediator.getWriteProcessStatuses().add(this.writeProcessStatus);

        processorMediator.getReadProcessCommunications().add(this.readProcessCommunication);
        processorMediator.getWriteProcessCommunications().add(this.writeProcessCommunication);

        processorMediator.getReadProcessContexts().add(this.readProcessContext);
        processorMediator.getWriteProcessContexts().add(this.writeProcessContext);

        processorMediator.getReadProcessHandleTables().add(this.readProcessHandleTable);
        processorMediator.getWriteProcessHandleTables().add(this.writeProcessHandleTable);

        processorMediator.getReadProcessStatistics().add(this.readProcessStatistics);
        processorMediator.getWriteProcessStatistics().add(this.writeProcessStatistics);

        processorMediator.getReadProcessTokens().add(this.readProcessToken);
        processorMediator.getWriteProcessTokens().add(this.writeProcessToken);
    }
}
