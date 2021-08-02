package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.objects.lang.*;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoProcessAndThreadStatisticsResolver extends APrototype implements IInfoResolver {
    public InfoProcessAndThreadStatisticsResolver() {
        this.dump = (dump, info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

            ProcessObject process = processManager.getCurrent();
            ThreadObject thread = threadManager.getCurrent();

            ProcessStatisticsObject processStatistics = process.getStatistics();
            ThreadStatisticsObject threadStatistics = thread.getStatistics();
            processStatistics.addInfoDump(1);
            threadStatistics.addInfoDump(1);

            ProcessTokenObject processToken = process.getToken();

            dump.setProcessID(process.getID());
            dump.setAccountID(processToken.getAccountID());

            return dump;
        };

        this.open = (handle, info, type, status, openAttribute, arguments) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

            ProcessObject process = processManager.getCurrent();
            ThreadObject thread = threadManager.getCurrent();

            ProcessStatisticsObject processStatistics = process.getStatistics();
            ThreadStatisticsObject threadStatistics = thread.getStatistics();
            processStatistics.addInfoOpen(1);
            threadStatistics.addInfoOpen(1);

            return handle;
        };

        this.close = (info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

            ProcessObject process = processManager.getCurrent();
            ThreadObject thread = threadManager.getCurrent();

            ProcessStatisticsObject processStatistics = process.getStatistics();
            ThreadStatisticsObject threadStatistics = thread.getStatistics();
            processStatistics.addInfoClose(1);
            threadStatistics.addInfoClose(1);
        };

        this.createChildAndOpen = (childInfo, info, type, status, childType, identification) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

            ProcessObject process = processManager.getCurrent();
            ThreadObject thread = threadManager.getCurrent();

            ProcessStatisticsObject processStatistics = process.getStatistics();
            ThreadStatisticsObject threadStatistics = thread.getStatistics();
            processStatistics.addInfoCreate(1);
            threadStatistics.addInfoCreate(1);

            return childInfo;
        };

        this.getOrRebuildChild = (childInfo, info, type, status, identification, open) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

            ProcessObject process = processManager.getCurrent();
            ThreadObject thread = threadManager.getCurrent();

            ProcessStatisticsObject processStatistics = process.getStatistics();
            ThreadStatisticsObject threadStatistics = thread.getStatistics();
            processStatistics.addInfoGet(1);
            threadStatistics.addInfoGet(1);

            return childInfo;
        };

        this.deleteChild = (info, type, status, identification) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

            ProcessObject process = processManager.getCurrent();
            ThreadObject thread = threadManager.getCurrent();

            ProcessStatisticsObject processStatistics = process.getStatistics();
            ThreadStatisticsObject threadStatistics = thread.getStatistics();
            processStatistics.addInfoDelete(1);
            threadStatistics.addInfoDelete(1);
        };

        this.queryChild = (summaryDefinitions, info, type, status, queryChild) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

            ProcessObject process = processManager.getCurrent();
            ThreadObject thread = threadManager.getCurrent();

            ProcessStatisticsObject processStatistics = process.getStatistics();
            ThreadStatisticsObject threadStatistics = thread.getStatistics();
            processStatistics.addInfoQuery(1);
            threadStatistics.addInfoQuery(1);

            return summaryDefinitions;
        };

        this.renameChild = (info, type, status, oldIdentification, newIdentification) -> {
        };

        this.readProperties = (properties, info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

            ProcessObject process = processManager.getCurrent();
            ThreadObject thread = threadManager.getCurrent();

            ProcessStatisticsObject processStatistics = process.getStatistics();
            ThreadStatisticsObject threadStatistics = thread.getStatistics();
            processStatistics.addInfoRead(1);
            threadStatistics.addInfoRead(1);

            return properties;
        };

        this.writeProperties = (info, type, status, properties) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

            ProcessObject process = processManager.getCurrent();
            ThreadObject thread = threadManager.getCurrent();

            ProcessStatisticsObject processStatistics = process.getStatistics();
            ThreadStatisticsObject threadStatistics = thread.getStatistics();
            processStatistics.addInfoWrite(1);
            threadStatistics.addInfoWrite(1);
        };

        this.readContent = (content, info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

            ProcessObject process = processManager.getCurrent();
            ThreadObject thread = threadManager.getCurrent();

            ProcessStatisticsObject processStatistics = process.getStatistics();
            ThreadStatisticsObject threadStatistics = thread.getStatistics();
            processStatistics.addInfoRead(1);
            threadStatistics.addInfoRead(1);

            return content;
        };

        this.writeContent = (info, type, status, content) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

            ProcessObject process = processManager.getCurrent();
            ThreadObject thread = threadManager.getCurrent();

            ProcessStatisticsObject processStatistics = process.getStatistics();
            ThreadStatisticsObject threadStatistics = thread.getStatistics();
            processStatistics.addInfoWrite(1);
            threadStatistics.addInfoWrite(1);
        };

        this.executeContent = (info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

            ProcessObject process = processManager.getCurrent();
            ThreadObject thread = threadManager.getCurrent();

            ProcessStatisticsObject processStatistics = process.getStatistics();
            ThreadStatisticsObject threadStatistics = thread.getStatistics();
            processStatistics.addInfoRead(1);
            threadStatistics.addInfoRead(1);
        };
    }

    private final InfoProcessorDumpFunction dump;
    private final InfoProcessorOpenFunction open;
    private final InfoProcessorCloseConsumer close;
    private final InfoProcessorCreateChildAndOpenFunction createChildAndOpen;
    private final InfoProcessorGetOrRebuildChildFunction getOrRebuildChild;
    private final InfoProcessorDeleteChildConsumer deleteChild;
    private final InfoProcessorQueryChildFunction queryChild;
    private final InfoProcessorRenameChildConsumer renameChild;
    private final InfoProcessorReadPropertyFunction readProperties;
    private final InfoProcessorWritePropertyConsumer writeProperties;
    private final InfoProcessorReadContentFunction readContent;
    private final InfoProcessorWriteContentConsumer writeContent;
    private final InfoProcessorExecuteContentConsumer executeContent;

    @Override
    public void resolve(InfoEntity info, InfoProcessorMediator processorMediator) {
        processorMediator.getDumps().add(this.dump);
        processorMediator.getOpens().add(this.open);
        processorMediator.getCloses().add(this.close);
        processorMediator.getCreateChildAndOpens().add(this.createChildAndOpen);
        processorMediator.getGetOrRebuildChilds().add(this.getOrRebuildChild);
        processorMediator.getDeleteChilds().add(this.deleteChild);
        processorMediator.getQueryChilds().add(this.queryChild);
        processorMediator.getRenameChilds().add(this.renameChild);
        processorMediator.getReadProperties().add(this.readProperties);
        processorMediator.getWriteProperties().add(this.writeProperties);
        processorMediator.getReadContents().add(this.readContent);
        processorMediator.getWriteContents().add(this.writeContent);
        processorMediator.getExecuteContents().add(this.executeContent);
    }

    @Override
    public int order() {
        return 3;
    }
}
