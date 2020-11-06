package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.functions.Consumer2;
import indi.sly.system.common.functions.Function2;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.processes.entities.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.ProcessObjectProcessorRegister;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessMemberGetAndSetProcessor extends ACoreObject implements IProcessObjectProcessor {
    private final Function2<byte[], byte[], ProcessEntity> readProcessHandleTable;
    private final Consumer2<ProcessEntity, byte[]> writeProcessHandleTable;
    private final Function2<byte[], byte[], ProcessEntity> readProcessStatistics;
    private final Consumer2<ProcessEntity, byte[]> writeProcessStatistics;
    private final Function2<byte[], byte[], ProcessEntity> readProcessToken;
    private final Consumer2<ProcessEntity, byte[]> writeProcessToken;

    public ProcessMemberGetAndSetProcessor() {
        this.readProcessHandleTable = (handleTable, process) -> process.getHandleTable();
        this.writeProcessHandleTable = ProcessEntity::setHandleTable;
        this.readProcessStatistics = (statistics, process) -> process.getStatistics();
        this.writeProcessStatistics = ProcessEntity::setStatistics;
        this.readProcessToken = (token, process) -> process.getToken();
        this.writeProcessToken = ProcessEntity::setToken;
    }

    @Override
    public void process(ProcessEntity process, ProcessObjectProcessorRegister processorRegister) {
        processorRegister.getReadProcessHandleTables().add(this.readProcessHandleTable);
        processorRegister.getWriteProcessHandleTables().add(this.writeProcessHandleTable);
        processorRegister.getReadProcessStatistics().add(this.readProcessStatistics);
        processorRegister.getWriteProcessStatistics().add(this.writeProcessStatistics);
        processorRegister.getReadProcessTokens().add(this.readProcessToken);
        processorRegister.getWriteProcessTokens().add(this.writeProcessToken);
    }
}
