package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.lang.StatusOverflowException;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.processes.lang.ProcessProcessorReadComponentFunction;
import indi.sly.system.kernel.processes.lang.ProcessProcessorReadStatusFunction;
import indi.sly.system.kernel.processes.lang.ProcessProcessorWriteComponentConsumer;
import indi.sly.system.kernel.processes.lang.ProcessProcessorWriteStatusConsumer;
import indi.sly.system.kernel.processes.prototypes.mediators.ProcessProcessorMediator;
import indi.sly.system.kernel.processes.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessMemberResolver extends AResolver implements IProcessResolver {
    private final ProcessProcessorReadStatusFunction readProcessStatus;
    private final ProcessProcessorWriteStatusConsumer writeProcessStatus;
    private final ProcessProcessorReadComponentFunction readProcessCommunication;
    private final ProcessProcessorWriteComponentConsumer writeProcessCommunication;
    private final ProcessProcessorReadComponentFunction readProcessContext;
    private final ProcessProcessorWriteComponentConsumer writeProcessContext;
    private final ProcessProcessorReadComponentFunction readProcessInfoTable;
    private final ProcessProcessorWriteComponentConsumer writeProcessInfoTable;
    private final ProcessProcessorReadComponentFunction readProcessSession;
    private final ProcessProcessorWriteComponentConsumer writeProcessSession;
    private final ProcessProcessorReadComponentFunction readProcessStatistics;
    private final ProcessProcessorWriteComponentConsumer writeProcessStatistics;
    private final ProcessProcessorReadComponentFunction readProcessToken;
    private final ProcessProcessorWriteComponentConsumer writeProcessToken;

    public ProcessMemberResolver() {
        this.readProcessStatus = (status, process) -> process.getStatus();
        this.writeProcessStatus = ProcessEntity::setStatus;

        this.readProcessCommunication = (communication, process) -> process.getCommunication();
        this.writeProcessCommunication = (process, communication) -> {
            process.setCommunication((ProcessCommunicationEntity) communication);
        };

        this.readProcessContext = (context, process) -> process.getContext();
        this.writeProcessContext = (process, context) -> {
            process.setContext((ProcessContextEntity) context);
        };

        this.readProcessInfoTable = (infoTable, process) -> process.getInfoTable();
        this.writeProcessInfoTable = (process, infoTable) -> {
            process.setInfoTable((ProcessInfoTableEntity) infoTable);
        };

        this.readProcessSession = (session, process) -> process.getSession();
        this.writeProcessSession = (process, session) -> {
            process.setSession((ProcessSessionEntity) session);
        };

        this.readProcessStatistics = (statistics, process) -> process.getStatistics();
        this.writeProcessStatistics = (process, statistics) -> {
            process.setStatistics((ProcessStatisticsEntity) statistics);
        };

        this.readProcessToken = (token, process) -> process.getToken();
        this.writeProcessToken = (process, token) -> {
            process.setToken((ProcessTokenEntity) token);
        };
    }

    @Override
    public void resolve(ProcessEntity process, ProcessProcessorMediator processorMediator) {
        processorMediator.getReadProcessStatuses().add(this.readProcessStatus);
        processorMediator.getWriteProcessStatuses().add(this.writeProcessStatus);

        processorMediator.getReadProcessCommunications().add(this.readProcessCommunication);
        processorMediator.getWriteProcessCommunications().add(this.writeProcessCommunication);

        processorMediator.getReadProcessContexts().add(this.readProcessContext);
        processorMediator.getWriteProcessContexts().add(this.writeProcessContext);

        processorMediator.getReadProcessInfoTables().add(this.readProcessInfoTable);
        processorMediator.getWriteProcessInfoTables().add(this.writeProcessInfoTable);

        processorMediator.getReadProcessSessions().add(this.readProcessSession);
        processorMediator.getWriteProcessSessions().add(this.writeProcessSession);

        processorMediator.getReadProcessStatistics().add(this.readProcessStatistics);
        processorMediator.getWriteProcessStatistics().add(this.writeProcessStatistics);

        processorMediator.getReadProcessTokens().add(this.readProcessToken);
        processorMediator.getWriteProcessTokens().add(this.writeProcessToken);
    }
}
