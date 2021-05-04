package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.lang.Consumer2;
import indi.sly.system.common.lang.Function2;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessMemberGetAndSetResolver extends APrototype implements IProcessResolver {
    private final Function2<Long, Long, ProcessEntity> readProcessStatus;
    private final Consumer2<ProcessEntity, Long> writeProcessStatus;
    private final Function2<byte[], byte[], ProcessEntity> readProcessCommunication;
    private final Consumer2<ProcessEntity, byte[]> writeProcessCommunication;
    private final Function2<byte[], byte[], ProcessEntity> readProcessContext;
    private final Consumer2<ProcessEntity, byte[]> writeProcessContext;
    private final Function2<byte[], byte[], ProcessEntity> readProcessHandleTable;
    private final Consumer2<ProcessEntity, byte[]> writeProcessHandleTable;
    private final Function2<byte[], byte[], ProcessEntity> readProcessStatistics;
    private final Consumer2<ProcessEntity, byte[]> writeProcessStatistics;
    private final Function2<byte[], byte[], ProcessEntity> readProcessToken;
    private final Consumer2<ProcessEntity, byte[]> writeProcessToken;

    public ProcessMemberGetAndSetResolver() {
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
