package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.StatusIsUsedException;
import indi.sly.system.common.lang.StatusOverflowException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.lang.*;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoTableObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoTypeInitializerResolver extends AInfoResolver {
    public InfoTypeInitializerResolver() {
        this.dump = (dump, info, type, status) -> {
            type.getInitializer().dumpProcedure(info, dump);

            return dump;
        };

        this.open = (index, info, type, status, openAttribute, arguments) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessInfoTableObject processInfoTable = process.getInfoTable();

            ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(info.getID());

            type.getInitializer().openProcedure(info, processInfoEntry.getOpen(), openAttribute, arguments);

            return index;
        };

        this.close = (info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessInfoTableObject processInfoTable = process.getInfoTable();

            ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(info.getID());

            type.getInitializer().closeProcedure(info, processInfoEntry.getOpen());
        };

        this.createChildAndOpen = (childInfo, info, type, status, childType, identification) -> {
            if (ObjectUtil.isAnyNull(childInfo)) {
                childInfo = new InfoEntity();
            }

            if (ValueUtil.isAnyNullOrEmpty(childInfo.getID())) {
                if (identification.getType().equals(UUID.class)) {
                    childInfo.setID(UUIDUtil.getFromBytes(identification.getID()));
                } else if (identification.getType().equals(String.class)) {
                    childInfo.setID(UUIDUtil.createRandom());
                }
            }
            if (ValueUtil.isAnyNullOrEmpty(childInfo.getType())) {
                childInfo.setType(childType);
            }
            childInfo.setOpened(0);
            if (ValueUtil.isAnyNullOrEmpty(childInfo.getName())) {
                if (identification.getType().equals(UUID.class)) {
                    childInfo.setName(null);
                } else if (identification.getType().equals(String.class)) {
                    childInfo.setName(StringUtil.readFormBytes(identification.getID()));
                }
            }
            Map<Long, Long> date = new HashMap<>();
            childInfo.setDate(ObjectUtil.transferToByteArray(date));
            if (ObjectUtil.isAnyNull(childInfo.getProperties())) {
                Map<String, String> childProperties = new HashMap<>();
                childInfo.setProperties(ObjectUtil.transferToByteArray(childProperties));
            }

            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
            AInfoTypeInitializer childTypeInitializer = typeManager.get(childInfo.getType()).getInitializer();

            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            UUID childRepositoryID = childTypeInitializer.getPoolID(childInfo.getID(), childInfo.getType());
            AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(childRepositoryID);
            childInfo = infoRepository.add(childInfo);

            childTypeInitializer.createProcedure(childInfo);

            AInfoTypeInitializer typeInitializer = type.getInitializer();
            typeInitializer.createChildProcedure(info, childInfo);

            return childInfo;
        };

        this.getOrRebuildChild = (childInfo, info, type, status, identification, open) -> {
            AInfoTypeInitializer typeInitializer = type.getInitializer();
            InfoSummaryDefinition infoSummary = typeInitializer.getChildProcedure(info, identification);

            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
            AInfoTypeInitializer childTypeInitializer = typeManager.get(infoSummary.getType()).getInitializer();

            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            UUID childRepositoryID = childTypeInitializer.getPoolID(infoSummary.getID(), infoSummary.getType());
            AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(childRepositoryID);
            childInfo = infoRepository.get(infoSummary.getID());

            childTypeInitializer.getProcedure(childInfo);

            return childInfo;
        };

        this.deleteChild = (info, type, status, identification) -> {
            InfoEntity childInfo = this.getOrRebuildChild.apply(null, info, type, status, identification, null);

            if (childInfo.getOpened() > 0) {
                throw new StatusIsUsedException();
            }

            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
            AInfoTypeInitializer childTypeInitializer = typeManager.get(childInfo.getType()).getInitializer();

            childTypeInitializer.deleteProcedure(childInfo);

            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            UUID childRepositoryID = childTypeInitializer.getPoolID(childInfo.getID(), childInfo.getType());
            AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(childRepositoryID);
            infoRepository.delete(childInfo);

            AInfoTypeInitializer typeInitializer = type.getInitializer();
            typeInitializer.deleteChildProcedure(info, identification);
        };

        this.queryChild = (infoSummaries, info, type, status, wildcard) -> {
            AInfoTypeInitializer typeInitializer = type.getInitializer();
            Set<InfoSummaryDefinition> infoSummary = typeInitializer.queryChildProcedure(info, wildcard);

            infoSummaries.addAll(infoSummary);

            return infoSummaries;
        };

        this.renameChild = (info, type, status, oldIdentification, newIdentification) -> {
            InfoEntity childInfo = this.getOrRebuildChild.apply(null, info, type, status, oldIdentification, null);

            if (childInfo.getOpened() > 0) {
                throw new StatusIsUsedException();
            }

            AInfoTypeInitializer typeInitializer = type.getInitializer();
            typeInitializer.renameChildProcedure(info, oldIdentification, newIdentification);

            if (newIdentification.getType().equals(String.class)) {
                childInfo.setName(StringUtil.readFormBytes(newIdentification.getID()));
            }
        };

        this.readProperties = (properties, info, type, status) -> {
            Map<String, String> newProperties = ObjectUtil.transferFromByteArray(info.getProperties());
            assert newProperties != null;

            properties.putAll(newProperties);

            return properties;
        };

        this.writeProperties = (info, type, status, properties) -> {
            Map<String, String> newProperties = new HashMap<>(properties);

            byte[] newPropertiesSource = ObjectUtil.transferToByteArray(newProperties);
            assert newPropertiesSource != null;

            if (newPropertiesSource.length > 1024) {
                throw new StatusOverflowException();
            }

            info.setProperties(newPropertiesSource);

            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessInfoTableObject processInfoTable = process.getInfoTable();

            AInfoTypeInitializer typeInitializer = type.getInitializer();
            if (processInfoTable.containByID(info.getID())) {
                ProcessInfoEntryObject processInfoEntry = processInfoTable.getByID(info.getID());

                typeInitializer.refreshPropertiesProcedure(info, processInfoEntry.getOpen());
            } else {
                typeInitializer.refreshPropertiesProcedure(info, null);
            }
        };

        this.readContent = (content, info, type, status) -> info.getContent();

        this.writeContent = (info, type, status, content) -> {
            if (content.length > 4096) {
                throw new StatusOverflowException();
            }

            info.setContent(content);
        };

        this.executeContent = (info, type, status) -> {
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
        return 2;
    }
}
