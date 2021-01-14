package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.lang.Consumer2;
import indi.sly.system.common.lang.Function2;
import indi.sly.system.kernel.core.prototypes.ACorePrototype;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.ProcessProcessorRegister;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessMemberGetAndSetProcessor extends ACorePrototype implements IProcessProcessor {
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

    public ProcessMemberGetAndSetProcessor() {
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
    public void process(ProcessEntity process, ProcessProcessorRegister processorRegister) {
        processorRegister.getReadProcessStatuses().add(this.readProcessStatus);
        processorRegister.getWriteProcessStatuses().add(this.writeProcessStatus);

        processorRegister.getReadProcessCommunications().add(this.readProcessCommunication);
        processorRegister.getWriteProcessCommunications().add(this.writeProcessCommunication);

        processorRegister.getReadProcessContexts().add(this.readProcessContext);
        processorRegister.getWriteProcessContexts().add(this.writeProcessContext);

        processorRegister.getReadProcessHandleTables().add(this.readProcessHandleTable);
        processorRegister.getWriteProcessHandleTables().add(this.writeProcessHandleTable);

        processorRegister.getReadProcessStatistics().add(this.readProcessStatistics);
        processorRegister.getWriteProcessStatistics().add(this.writeProcessStatistics);

        processorRegister.getReadProcessTokens().add(this.readProcessToken);
        processorRegister.getWriteProcessTokens().add(this.writeProcessToken);
    }
}
