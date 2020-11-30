package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.exceptions.StatusRelationshipErrorException;
import indi.sly.system.common.functions.Consumer2;
import indi.sly.system.common.functions.Function2;
import indi.sly.system.common.utility.LogicalUtils;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.processes.entities.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.ProcessObjectProcessorRegister;
import indi.sly.system.kernel.processes.types.ProcessStatusTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StatusCheckProcessor extends ACoreObject implements IProcessObjectProcessor {
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

    public StatusCheckProcessor() {
        this.readProcessStatus = (status, process) -> status;
        this.writeProcessStatus = (process, status) -> {
            if (!LogicalUtils.isAnyEqual(status, ProcessStatusTypes.INITIALIZATION,
                    ProcessStatusTypes.RUNNING, ProcessStatusTypes.INTERRUPTED, ProcessStatusTypes.DIED)) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.readProcessCommunication = (communication, process) -> {
            if (!LogicalUtils.isAnyEqual(process.getStatus(), ProcessStatusTypes.INITIALIZATION,
                    ProcessStatusTypes.RUNNING, ProcessStatusTypes.DIED)) {
                throw new StatusRelationshipErrorException();
            }

            return process.getCommunication();
        };
        this.writeProcessCommunication = (process, communication) -> {
            if (!LogicalUtils.isAnyEqual(process.getStatus(), ProcessStatusTypes.INITIALIZATION,
                    ProcessStatusTypes.RUNNING, ProcessStatusTypes.DIED)) {
                throw new StatusRelationshipErrorException();
            }

            process.setCommunication(communication);
        };

        this.readProcessContext = (context, process) -> {
            if (!LogicalUtils.isAnyEqual(process.getStatus(), ProcessStatusTypes.INITIALIZATION,
                    ProcessStatusTypes.RUNNING, ProcessStatusTypes.DIED)) {
                throw new StatusRelationshipErrorException();
            }

            return process.getContext();
        };
        this.writeProcessContext = (process, context) -> {
            if (!LogicalUtils.isAnyEqual(process.getStatus(), ProcessStatusTypes.INITIALIZATION,
                    ProcessStatusTypes.RUNNING, ProcessStatusTypes.DIED)) {
                throw new StatusRelationshipErrorException();
            }

            process.setContext(context);
        };

        this.readProcessHandleTable = (handleTable, process) -> {
            if (!LogicalUtils.isAnyEqual(process.getStatus(), ProcessStatusTypes.INITIALIZATION,
                    ProcessStatusTypes.RUNNING, ProcessStatusTypes.DIED)) {
                throw new StatusRelationshipErrorException();
            }

            return process.getHandleTable();
        };
        this.writeProcessHandleTable = (process, handleTable) -> {
            if (!LogicalUtils.isAnyEqual(process.getStatus(), ProcessStatusTypes.INITIALIZATION,
                    ProcessStatusTypes.RUNNING, ProcessStatusTypes.DIED)) {
                throw new StatusRelationshipErrorException();
            }

            process.setHandleTable(handleTable);
        };

        this.readProcessStatistics = (statistics, process) -> {
            if (!LogicalUtils.isAnyEqual(process.getStatus(), ProcessStatusTypes.INITIALIZATION,
                    ProcessStatusTypes.RUNNING, ProcessStatusTypes.DIED)) {
                throw new StatusRelationshipErrorException();
            }

            return process.getStatistics();
        };
        this.writeProcessStatistics = (process, statistics) -> {
            if (!LogicalUtils.isAnyEqual(process.getStatus(), ProcessStatusTypes.INITIALIZATION,
                    ProcessStatusTypes.RUNNING, ProcessStatusTypes.DIED)) {
                throw new StatusRelationshipErrorException();
            }

            process.setStatistics(statistics);
        };

        this.readProcessToken = (token, process) -> {
            if (!LogicalUtils.isAnyEqual(process.getStatus(), ProcessStatusTypes.INITIALIZATION,
                    ProcessStatusTypes.RUNNING, ProcessStatusTypes.DIED)) {
                throw new StatusRelationshipErrorException();
            }

            return process.getToken();
        };
        this.writeProcessToken = (process, token) -> {
            if (!LogicalUtils.isAnyEqual(process.getStatus(), ProcessStatusTypes.INITIALIZATION,
                    ProcessStatusTypes.RUNNING, ProcessStatusTypes.DIED)) {
                throw new StatusRelationshipErrorException();
            }

            process.setToken(token);
        };
    }

    @Override
    public void process(ProcessEntity process, ProcessObjectProcessorRegister processorRegister) {
        processorRegister.getReadProcessStatuses().add(readProcessStatus);
        processorRegister.getWriteProcessStatuses().add(writeProcessStatus);

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
