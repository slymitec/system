package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.lang.*;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessHandleEntryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessHandleTableObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;
import java.util.Map.Entry;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TypeInitializerResolver extends APrototype implements IInfoResolver {
    public TypeInitializerResolver() {
        this.dump = (dump, info, type, status) -> {
            type.getTypeInitializer().dumpProcedure(info, dump);

            return dump;
        };

        this.open = (handle, info, type, status, openAttribute, arguments) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessHandleTableObject processHandleTable = process.getHandleTable();

            ProcessHandleEntryObject processHandleEntry = processHandleTable.getByInfoID(info.getID());

            type.getTypeInitializer().openProcedure(info, processHandleEntry.getOpen(), openAttribute, arguments);

            return handle;
        };

        this.close = (info, type, status) -> {
            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessHandleTableObject processHandleTable = process.getHandleTable();

            ProcessHandleEntryObject processHandleEntry = processHandleTable.getByInfoID(info.getID());

            type.getTypeInitializer().closeProcedure(info, processHandleEntry.getOpen());
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
            childInfo.setOccupied(0);
            childInfo.setOpened(0);
            if (ValueUtil.isAnyNullOrEmpty(childInfo.getName())) {
                if (identification.getType().equals(UUID.class)) {
                    childInfo.setName(null);
                } else if (identification.getType().equals(String.class)) {
                    childInfo.setName(StringUtil.readFormBytes(identification.getID()));
                }
            }
            if (ObjectUtil.isAnyNull(childInfo.getProperties())) {
                Map<String, String> childProperties = new HashMap<>();
                childInfo.setProperties(ObjectUtil.transferToByteArray(childProperties));
            }

            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
            TypeObject childTypeObject = typeManager.get(childInfo.getType());
            childTypeObject.getTypeInitializer().createProcedure(childInfo);

            type.getTypeInitializer().createChildProcedure(info, childInfo);

            return childInfo;
        };

        this.getOrRebuildChild = (childInfo, info, type, status, identification, open) -> {
            InfoSummaryDefinition infoSummary = type.getTypeInitializer().getChildProcedure(info, identification);

            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
            TypeObject childType = typeManager.get(infoSummary.getType());

            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            UUID childRepositoryID = childType.getTypeInitializer().getPoolID(infoSummary.getID(),
                    infoSummary.getType());
            AInfoRepositoryObject entityRepository = memoryManager.getInfoRepository(childRepositoryID);
            childInfo = entityRepository.get(infoSummary.getID());

            childType.getTypeInitializer().getProcedure(childInfo);

            return childInfo;
        };

        this.deleteChild = (info, type, status, identification) -> {
            InfoEntity childInfo = this.getOrRebuildChild.apply(null, info, type, status, identification, null);

            if (childInfo.getOccupied() > 0 || childInfo.getOpened() > 0) {
                throw new StatusIsUsedException();
            }

            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
            TypeObject childType = typeManager.get(childInfo.getType());

            childType.getTypeInitializer().deleteProcedure(childInfo);

            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            UUID childRepositoryID = childType.getTypeInitializer().getPoolID(childInfo.getID(), childInfo.getType());
            AInfoRepositoryObject entityRepository = memoryManager.getInfoRepository(childRepositoryID);
            entityRepository.delete(childInfo);

            type.getTypeInitializer().deleteChildProcedure(info, identification);
        };

        this.queryChild = (summaryDefinitions, info, type, status, queryChild) -> {
            Set<InfoSummaryDefinition> TypeInitializerSummaryDefinitions =
                    type.getTypeInitializer().queryChildProcedure(info, queryChild);

            summaryDefinitions.addAll(TypeInitializerSummaryDefinitions);

            return summaryDefinitions;
        };

        this.renameChild = (info, type, status, oldIdentification, newIdentification) -> {
            InfoEntity childInfo = this.getOrRebuildChild.apply(null, info, type, status, oldIdentification, null);

            if (childInfo.getOccupied() > 0 || childInfo.getOpened() > 0) {
                throw new StatusIsUsedException();
            }

            type.getTypeInitializer().renameChildProcedure(info, oldIdentification, newIdentification);

            if (newIdentification.getType().equals(String.class)) {
                childInfo.setName(StringUtil.readFormBytes(newIdentification.getID()));
            }
        };

        this.readProperties = (properties, info, type, status) -> {
            Map<String, String> newProperties = ObjectUtil.transferFromByteArray(info.getProperties());

            for (Entry<String, String> pair : newProperties.entrySet()) {
                properties.put(pair.getKey(), pair.getValue());
            }

            return properties;
        };

        this.writeProperties = (info, type, status, properties) -> {
            Map<String, String> newProperties = new HashMap<>();

            for (Entry<String, String> pair : properties.entrySet()) {
                newProperties.put(pair.getKey(), pair.getValue());
            }

            byte[] newPropertiesSource = ObjectUtil.transferToByteArray(newProperties);

            if (newPropertiesSource.length > 1024) {
                throw new StatusInsufficientResourcesException();
            }

            info.setProperties(newPropertiesSource);

            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();
            ProcessHandleTableObject processHandleTable = process.getHandleTable();

            if (processHandleTable.containByInfoID(info.getID())) {
                ProcessHandleEntryObject processHandleEntry = processHandleTable.getByInfoID(info.getID());

                type.getTypeInitializer().refreshPropertiesProcedure(info, processHandleEntry.getOpen());
            } else {
                type.getTypeInitializer().refreshPropertiesProcedure(info, null);
            }
        };

        this.readContent = (content, info, type, status) -> info.getContent();

        this.writeContent = (info, type, status, content) -> {
            if (content.length > 1024) {
                throw new StatusInsufficientResourcesException();
            }

            info.setContent(content);
        };

        this.executeContent = (info, type, status) -> {

        };
    }

    private final DumpFunction dump;
    private final OpenFunction open;
    private final CloseConsumer close;
    private final CreateChildAndOpenFunction createChildAndOpen;
    private final GetOrRebuildChildFunction getOrRebuildChild;
    private final DeleteChildConsumer deleteChild;
    private final QueryChildFunction queryChild;
    private final RenameChildConsumer renameChild;
    private final ReadPropertyFunction readProperties;
    private final WritePropertyConsumer writeProperties;
    private final ReadContentFunction readContent;
    private final WriteContentConsumer writeContent;
    private final ExecuteContentConsumer executeContent;

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
