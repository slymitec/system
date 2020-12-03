package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.exceptions.StatusAlreadyFinishedException;
import indi.sly.system.common.exceptions.StatusNotSupportedException;
import indi.sly.system.common.exceptions.StatusRelationshipErrorException;
import indi.sly.system.common.functions.*;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.entities.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.prototypes.InfoObjectProcessorRegister;
import indi.sly.system.kernel.objects.definitions.InfoStatusDefinition;
import indi.sly.system.kernel.objects.definitions.InfoStatusOpenDefinition;
import indi.sly.system.kernel.objects.types.InfoStatusOpenAttributeTypes;
import indi.sly.system.kernel.objects.infotypes.types.TypeInitializerAttributeTypes;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConditionCheckProcessor extends ACoreObject implements IInfoObjectProcessor {
    public ConditionCheckProcessor() {
        this.open = (handle, info, type, status, openAttribute, arguments) -> {
            if (status.getOpen().getAttribute() != InfoStatusOpenAttributeTypes.CLOSE) {
                throw new StatusAlreadyFinishedException();
            }
            if (openAttribute == InfoStatusOpenAttributeTypes.CLOSE
                    || (openAttribute == InfoStatusOpenAttributeTypes.OPEN_EXCLUSIVE && info.getOpened() > 0)
                    || (openAttribute == InfoStatusOpenAttributeTypes.OPEN_ONLYREAD && info.getOpened() > 0
                    && !type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.CAN_BE_SHARED_READ))
                    || (openAttribute == InfoStatusOpenAttributeTypes.OPEN_SHARED_WRITE
                    && !type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.CAN_BE_SHARED_WRITE))) {
                throw new StatusNotSupportedException();
            }

            return handle;
        };

        this.close = (info, type, status) -> {
            if (status.getOpen().getAttribute() == InfoStatusOpenAttributeTypes.CLOSE) {
                throw new StatusAlreadyFinishedException();
            }
        };

        this.createChildAndOpen = (childInfo, info, type, status, childType, identification) -> {
            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_CHILD)
                    || (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.CHILD_IS_NAMELESS) && identification.getType() == UUID.class)) {
                throw new StatusNotSupportedException();
            }
            Set<UUID> childTypes = type.getChildTypes();
            if (!childTypes.contains(UUIDUtils.getEmpty()) && !childTypes.contains(childType)) {
                throw new StatusNotSupportedException();
            }

            return childInfo;
        };

        this.getOrRebuildChild = (childInfo, info, type, status, identification, statusOpen) -> {
            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_CHILD)
                    || (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.CHILD_IS_NAMELESS) && identification.getType() == UUID.class)) {
                throw new StatusNotSupportedException();
            }

            return childInfo;
        };

        this.deleteChild = (info, type, status, identification) -> {
            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_CHILD)
                    || (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.CHILD_IS_NAMELESS) && identification.getType() == UUID.class)) {
                throw new StatusNotSupportedException();
            }
        };

        this.queryChild = (summaryDefinitions, info, type, status, queryChild) -> {
            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_CHILD)) {
                throw new StatusNotSupportedException();
            }

            return summaryDefinitions;
        };

        this.renameChild = (info, type, status, oldIdentification, newIdentification) -> {
            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_CHILD) || (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.CHILD_IS_NAMELESS))) {
                throw new StatusNotSupportedException();
            }
            if (oldIdentification.getType() == UUID.class || newIdentification.getType() == UUID.class) {
                throw new StatusNotSupportedException();
            }
        };

        this.readProperties = (properties, info, type, status) -> {
            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PROPERTIES)) {
                throw new StatusNotSupportedException();
            }

            return properties;
        };

        this.writeProperties = (info, type, status, properties) -> {
            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PROPERTIES)) {
                throw new StatusNotSupportedException();
            }
        };

        this.readContent = (content, info, type, status) -> {
            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_CONTENT)) {
                throw new StatusNotSupportedException();
            }
            if (status.getOpen().getAttribute() == InfoStatusOpenAttributeTypes.CLOSE) {
                throw new StatusRelationshipErrorException();
            }

            return content;
        };

        this.writeContent = (info, type, status, content) -> {
            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_CONTENT)) {
                throw new StatusNotSupportedException();
            }
            if (status.getOpen().getAttribute() == InfoStatusOpenAttributeTypes.CLOSE) {
                throw new StatusRelationshipErrorException();
            }
        };
    }

    private final Function6<UUID, UUID, InfoEntity, TypeObject, InfoStatusDefinition, Long, Object[]> open;
    private final Consumer3<InfoEntity, TypeObject, InfoStatusDefinition> close;
    private final Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, InfoStatusDefinition, UUID,
            Identification> createChildAndOpen;
    private final Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, InfoStatusDefinition, Identification,
            InfoStatusOpenDefinition> getOrRebuildChild;
    private final Consumer4<InfoEntity, TypeObject, InfoStatusDefinition, Identification> deleteChild;
    private final Function5<Set<InfoSummaryDefinition>, Set<InfoSummaryDefinition>, InfoEntity, TypeObject,
            InfoStatusDefinition, Predicate<InfoSummaryDefinition>> queryChild;
    private final Consumer5<InfoEntity, TypeObject, InfoStatusDefinition, Identification, Identification> renameChild;
    private final Function4<Map<String, String>, Map<String, String>, InfoEntity, TypeObject, InfoStatusDefinition> readProperties;
    private final Consumer4<InfoEntity, TypeObject, InfoStatusDefinition, Map<String, String>> writeProperties;
    private final Function4<byte[], byte[], InfoEntity, TypeObject, InfoStatusDefinition> readContent;
    private final Consumer4<InfoEntity, TypeObject, InfoStatusDefinition, byte[]> writeContent;

    @Override
    public void process(InfoEntity info, InfoObjectProcessorRegister processorRegister) {
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
