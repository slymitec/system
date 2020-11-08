package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.exceptions.StatusInsufficientResourcesException;
import indi.sly.system.common.exceptions.StatusIsUsedException;
import indi.sly.system.common.functions.*;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.StringUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.entities.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.prototypes.InfoObjectProcessorRegister;
import indi.sly.system.kernel.objects.prototypes.StatusDefinition;
import indi.sly.system.kernel.objects.prototypes.StatusOpenDefinition;
import indi.sly.system.kernel.objects.types.prototypes.TypeObject;
import indi.sly.system.kernel.objects.prototypes.DumpDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TypeInitializerProcessor extends ACoreObject implements IInfoObjectProcessor {
    public TypeInitializerProcessor() {
        this.dump = (dump, info, type, status) -> {
            type.getTypeInitializer().dumpProcedure(info, dump);

            return dump;
        };

        this.open = (handle, info, type, status, openAttribute, arguments) -> {
            type.getTypeInitializer().openProcedure(info, status.getOpen(), openAttribute, arguments);

            return handle;
        };

        this.close = (info, type, status) -> type.getTypeInitializer().closeProcedure(info, status.getOpen());

        this.createChildAndOpen = (childInfo, info, type, status, childType, identification) -> {
            if (ObjectUtils.isAnyNull(childInfo)) {
                childInfo = new InfoEntity();
            }

            if (UUIDUtils.isAnyNullOrEmpty(childInfo.getID())) {
                if (identification.getType().equals(UUID.class)) {
                    childInfo.setID(UUIDUtils.getFromBytes(identification.getID()));
                } else if (identification.getType().equals(String.class)) {
                    childInfo.setID(UUIDUtils.createRandom());
                }
            }
            if (UUIDUtils.isAnyNullOrEmpty(childInfo.getType())) {
                childInfo.setType(childType);
            }
            childInfo.setOccupied(0);
            childInfo.setOpened(0);
            if (StringUtils.isAnyNullOrEmpty(childInfo.getName())) {
                if (identification.getType().equals(UUID.class)) {
                    childInfo.setName(null);
                } else if (identification.getType().equals(String.class)) {
                    childInfo.setName(StringUtils.readFormBytes(identification.getID()));
                }
            }
            if (ObjectUtils.isAnyNull(childInfo.getProperties())) {
                Map<String, String> childProperties = new HashMap<>();
                childInfo.setProperties(ObjectUtils.transferToByteArray(childProperties));
            }

            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
            TypeObject childTypeObject = typeManager.get(childInfo.getType());
            childTypeObject.getTypeInitializer().createProcedure(childInfo);

            type.getTypeInitializer().createChildProcedure(info, childInfo);

            return childInfo;
        };

        this.getOrRebuildChild = (childInfo, info, type, status, identification, statusOpen) -> {
            InfoSummaryDefinition infoSummary = type.getTypeInitializer().getChildProcedure(info, identification);

            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
            TypeObject childType = typeManager.get(infoSummary.getType());

            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            UUID childRepositoryID = childType.getTypeInitializer().getPoolID(infoSummary.getID(), infoSummary.getType());
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
            Set<InfoSummaryDefinition> TypeInitializerSummaryDefinitions = type.getTypeInitializer().queryChildProcedure(info, queryChild);

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
                childInfo.setName(StringUtils.readFormBytes(newIdentification.getID()));
            }
        };

        this.readProperties = (properties, info, type, status) -> {
            Map<String, String> newProperties = ObjectUtils.transferFromByteArray(info.getProperties());

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

            byte[] newPropertiesSource = ObjectUtils.transferToByteArray(newProperties);

            if (newPropertiesSource.length > 1024) {
                throw new StatusInsufficientResourcesException();
            }

            info.setProperties(newPropertiesSource);

            type.getTypeInitializer().refreshPropertiesProcedure(info, status.getOpen());
        };

        this.readContent = (content, info, type, status) -> info.getContent();

        this.writeContent = (info, type, status, content) -> {
            if (content.length > 1024) {
                throw new StatusInsufficientResourcesException();
            }

            info.setContent(content);
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
