package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.StatusIsUsedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.lang.*;
import indi.sly.system.kernel.objects.prototypes.mediators.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessInfoTableObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoTypeInitializerResolver extends AResolver implements IInfoResolver {
    public InfoTypeInitializerResolver() {
        this.dump = (dump, info, type, cache) -> {
            type.getInitializer().dumpProcedure(info, dump);

            return dump;
        };

        this.open = (index, info, type, cache, openAttribute, arguments) -> {
            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessInfoTableObject processInfoTable = process.getInfoTable();

            ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(info.getId());

            type.getInitializer().openProcedure(info, processInfoEntry.getOpen(), arguments);

            return index;
        };

        this.close = (info, type, status) -> {
            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessInfoTableObject processInfoTable = process.getInfoTable();

            ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(info.getId());

            type.getInitializer().closeProcedure(info, processInfoEntry.getOpen());

            return info;
        };

        this.createChild = (childInfo, info, type, cache, childType, identification) -> {
            if (ObjectUtil.isAnyNull(childInfo)) {
                childInfo = new InfoEntity();
            }

            if (ValueUtil.isAnyNullOrEmpty(childInfo.getId())) {
                if (identification.type().equals(UUID.class)) {
                    childInfo.setId(UUIDUtil.readFormBytes(identification.value()));
                } else if (identification.type().equals(String.class)) {
                    childInfo.setId(UUIDUtil.createRandom());
                }
            }
            if (ValueUtil.isAnyNullOrEmpty(childInfo.getType())) {
                childInfo.setType(childType);
            }
            childInfo.setOpened(0);
            if (ValueUtil.isAnyNullOrEmpty(childInfo.getName())) {
                if (identification.type().equals(UUID.class)) {
                    childInfo.setName(null);
                } else if (identification.type().equals(String.class)) {
                    childInfo.setName(StringUtil.readFormBytes(identification.value()));
                }
            }
            Map<Long, Long> date = new HashMap<>();
            childInfo.setDate(date);
            if (ObjectUtil.isAnyNull(childInfo.getProperties())) {
                childInfo.setProperties(new HashMap<>());
            }

            TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
            AInfoTypeInitializer childTypeInitializer = typeManager.get(childInfo.getType()).getInitializer();

            MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
            UUID childRepositoryId = childTypeInitializer.getPoolId(childInfo.getId(), childInfo.getType());
            AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(childRepositoryId);
            childInfo = infoRepository.add(childInfo);

            childTypeInitializer.createProcedure(childInfo);

            AInfoTypeInitializer typeInitializer = type.getInitializer();
            typeInitializer.createChildProcedure(info, childInfo);

            return childInfo;
        };

        this.getChild = (childInfo, info, type, cache, identification) -> {
            AInfoTypeInitializer typeInitializer = type.getInitializer();
            InfoSummaryDefinition infoSummary = typeInitializer.getChildProcedure(info, identification);

            TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
            AInfoTypeInitializer childTypeInitializer = typeManager.get(infoSummary.getType()).getInitializer();

            MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
            UUID childRepositoryId = childTypeInitializer.getPoolId(infoSummary.getId(), infoSummary.getType());
            AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(childRepositoryId);
            childInfo = infoRepository.get(infoSummary.getId());

            childTypeInitializer.getProcedure(childInfo, identification);

            return childInfo;
        };

        this.deleteChild = (info, type, cache, identification) -> {
            InfoEntity childInfo = this.getChild.apply(null, info, type, cache, identification);

            if (childInfo.getOpened() > 0) {
                throw new StatusIsUsedException();
            }

            TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
            AInfoTypeInitializer childTypeInitializer = typeManager.get(childInfo.getType()).getInitializer();

            childTypeInitializer.deleteProcedure(childInfo);

            MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
            UUID childRepositoryId = childTypeInitializer.getPoolId(childInfo.getId(), childInfo.getType());
            AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(childRepositoryId);
            infoRepository.delete(childInfo);

            AInfoTypeInitializer typeInitializer = type.getInitializer();
            typeInitializer.deleteChildProcedure(info, identification);
        };

        this.queryChild = (infoSummaries, info, type, cache, wildcard) -> {
            AInfoTypeInitializer typeInitializer = type.getInitializer();
            Set<InfoSummaryDefinition> infoSummary = typeInitializer.queryChildProcedure(info, wildcard);

            infoSummaries.addAll(infoSummary);

            return infoSummaries;
        };

        this.renameChild = (info, type, cache, oldIdentification, newIdentification) -> {
            InfoEntity childInfo = this.getChild.apply(null, info, type, cache, oldIdentification);

            if (childInfo.getOpened() > 0) {
                throw new StatusIsUsedException();
            }

            AInfoTypeInitializer typeInitializer = type.getInitializer();
            typeInitializer.renameChildProcedure(info, oldIdentification, newIdentification);

            if (newIdentification.type().equals(String.class)) {
                childInfo.setName(StringUtil.readFormBytes(newIdentification.value()));
            }
        };

        this.readProperties = (properties, info, type, cache) -> {
            AInfoTypeInitializer typeInitializer = type.getInitializer();

            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessInfoTableObject processInfoTable = process.getInfoTable();

            Map<String, String> newProperties;
            if (processInfoTable.containById(info.getId())) {
                ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(info.getId());

                newProperties = typeInitializer.readPropertiesProcedure(info, processInfoEntry.getOpen());
            } else {
                newProperties = typeInitializer.readPropertiesProcedure(info, null);
            }
            properties.putAll(newProperties);

            return properties;
        };

        this.writeProperties = (info, type, cache, properties) -> {
            AInfoTypeInitializer typeInitializer = type.getInitializer();

            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessInfoTableObject processInfoTable = process.getInfoTable();

            if (processInfoTable.containById(info.getId())) {
                ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(info.getId());

                typeInitializer.writePropertiesProcedure(info, new HashMap<>(properties), processInfoEntry.getOpen());
            } else {
                typeInitializer.writePropertiesProcedure(info, new HashMap<>(properties), null);
            }
        };

        this.readContent = (content, info, type, cache) -> {
            AInfoTypeInitializer typeInitializer = type.getInitializer();

            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessInfoTableObject processInfoTable = process.getInfoTable();

            if (processInfoTable.containById(info.getId())) {
                ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(info.getId());

                return typeInitializer.readContentProcedure(info, processInfoEntry.getOpen());
            } else {
                return typeInitializer.readContentProcedure(info, null);
            }
        };

        this.writeContent = (info, type, cache, content) -> {
            AInfoTypeInitializer typeInitializer = type.getInitializer();

            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessInfoTableObject processInfoTable = process.getInfoTable();

            if (processInfoTable.containById(info.getId())) {
                ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(info.getId());

                typeInitializer.writeContentProcedure(info, processInfoEntry.getOpen(), content);
            } else {
                typeInitializer.writeContentProcedure(info, null, content);
            }
        };

        this.executeContent = (info, type, cache) -> {
            AInfoTypeInitializer typeInitializer = type.getInitializer();

            ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessInfoTableObject processInfoTable = process.getInfoTable();

            if (processInfoTable.containById(info.getId())) {
                ProcessInfoEntryObject processInfoEntry = processInfoTable.getById(info.getId());

                typeInitializer.executeContentProcedure(info, processInfoEntry.getOpen());
            } else {
                typeInitializer.executeContentProcedure(info, null);
            }
        };
    }

    private final InfoProcessorDumpFunction dump;
    private final InfoProcessorOpenFunction open;
    private final InfoProcessorCloseFunction close;
    private final InfoProcessorCreateChildFunction createChild;
    private final InfoProcessorGetChildFunction getChild;
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
        processorMediator.getCreateChildren().add(this.createChild);
        processorMediator.getGetChildren().add(this.getChild);
        processorMediator.getDeleteChildren().add(this.deleteChild);
        processorMediator.getQueryChildren().add(this.queryChild);
        processorMediator.getRenameChildren().add(this.renameChild);
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
