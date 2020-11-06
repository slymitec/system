package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.functions.*;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.entities.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.prototypes.InfoObjectProcessorRegister;
import indi.sly.system.kernel.objects.prototypes.StatusDefinition;
import indi.sly.system.kernel.objects.prototypes.StatusOpenDefinition;
import indi.sly.system.kernel.objects.types.TypeObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.objects.prototypes.DumpDefinition;
import indi.sly.system.kernel.processes.prototypes.ProcessHandleTableObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessStatisticsObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessProcessor extends ACoreObject implements IInfoObjectProcessor {
    public ProcessProcessor() {
        this.dump = (dump, info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessObject process = processManager.getCurrentProcess();
            ProcessStatisticsObject processStatistics = process.getStatistics();
            processStatistics.addInfoDump(1);

            ProcessTokenObject processToken = process.getToken();

            dump.setProcessID(process.getID());
            dump.setAccountID(processToken.getAccountID());

            return dump;
        };

        this.open = (handle, info, type, status, openAttribute, arguments) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessObject process = processManager.getCurrentProcess();
            ProcessHandleTableObject processHandleTable = process.getHandleTable();
            handle = processHandleTable.addInfo(status);

            ProcessStatisticsObject processStatistics = process.getStatistics();
            processStatistics.addInfoOpen(1);

            return handle;
        };

        this.close = (info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessObject process = processManager.getCurrentProcess();
            ProcessHandleTableObject processHandleTable = process.getHandleTable();
            processHandleTable.deleteInfo(status.getHandle());

            ProcessStatisticsObject processStatistics = process.getStatistics();
            processStatistics.addInfoClose(1);
        };

        this.createChildAndOpen = (childInfo, info, type, status, childType, identification) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessObject process = processManager.getCurrentProcess();
            ProcessStatisticsObject processStatistics = process.getStatistics();
            processStatistics.addInfoCreate(1);

            return childInfo;
        };

        this.getOrRebuildChild = (childInfo, info, type, status, identification, statusOpen) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessObject process = processManager.getCurrentProcess();
            ProcessStatisticsObject processStatistics = process.getStatistics();
            processStatistics.addInfoGet(1);

            return childInfo;
        };

        this.deleteChild = (info, type, status, identification) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessObject process = processManager.getCurrentProcess();
            ProcessStatisticsObject processStatistics = process.getStatistics();
            processStatistics.addInfoDelete(1);
        };

        this.queryChild = (summaryDefinitions, info, type, status, queryChild) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessObject process = processManager.getCurrentProcess();
            ProcessStatisticsObject processStatistics = process.getStatistics();
            processStatistics.addInfoQuery(1);

            return summaryDefinitions;
        };

        this.renameChild = (info, type, status, oldIdentification, newIdentification) -> {
        };

        this.readProperties = (properties, info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessObject process = processManager.getCurrentProcess();
            ProcessStatisticsObject processStatistics = process.getStatistics();
            processStatistics.addInfoRead(1);

            return properties;
        };

        this.writeProperties = (info, type, status, properties) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessObject process = processManager.getCurrentProcess();
            ProcessStatisticsObject processStatistics = process.getStatistics();
            processStatistics.addInfoWrite(1);
        };

        this.readContent = (content, info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessObject process = processManager.getCurrentProcess();
            ProcessStatisticsObject processStatistics = process.getStatistics();
            processStatistics.addInfoRead(1);

            return content;
        };

        this.writeContent = (info, type, status, content) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

            ProcessObject process = processManager.getCurrentProcess();
            ProcessStatisticsObject processStatistics = process.getStatistics();
            processStatistics.addInfoWrite(1);
        };
    }

    private final Function4<DumpDefinition, DumpDefinition, InfoEntity, TypeObject, StatusDefinition> dump;
    private final Function6<UUID, UUID, InfoEntity, TypeObject, StatusDefinition, Long, Object[]> open;
    private final Consumer3<InfoEntity, TypeObject, StatusDefinition> close;
    private final Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, StatusDefinition, UUID, Identification> createChildAndOpen;
    private final Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, StatusDefinition, Identification, StatusOpenDefinition> getOrRebuildChild;
    private final Consumer4<InfoEntity, TypeObject, StatusDefinition, Identification> deleteChild;
    private final Function5<Set<InfoSummaryDefinition>, Set<InfoSummaryDefinition>, InfoEntity, TypeObject, StatusDefinition, Predicate<InfoSummaryDefinition>> queryChild;
    private final Consumer5<InfoEntity, TypeObject, StatusDefinition, Identification, Identification> renameChild;
    private final Function4<Map<String, String>, Map<String, String>, InfoEntity, TypeObject, StatusDefinition> readProperties;
    private final Consumer4<InfoEntity, TypeObject, StatusDefinition, Map<String, String>> writeProperties;
    private final Function4<byte[], byte[], InfoEntity, TypeObject, StatusDefinition> readContent;
    private final Consumer4<InfoEntity, TypeObject, StatusDefinition, byte[]> writeContent;

    @Override
    public void process(InfoEntity info, InfoObjectProcessorRegister processorRegister) {
        processorRegister.getDumps().add(this.dump);
        processorRegister.getOpens().add(this.open);
        processorRegister.getCloses().add(this.close);
        processorRegister.getCreateChildAndOpens().add(this.createChildAndOpen);
        processorRegister.getGetOrRebuildChilds().add(this.getOrRebuildChild);
        processorRegister.getDeleteChilds().add(this.deleteChild);
        processorRegister.getQueryChilds().add(this.queryChild);
        processorRegister.getRenameChilds().add(this.renameChild);
        processorRegister.getReadProperties().add(this.readProperties);
        processorRegister.getWriteProperties().add(this.writeProperties);
        processorRegister.getReadContents().add(this.readContent);
        processorRegister.getWriteContents().add(this.writeContent);
    }

}
