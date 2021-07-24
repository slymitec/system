package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.objects.lang.*;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoStatusOpenAttributeType;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConditionCheckResolver extends APrototype implements IInfoResolver {
    public ConditionCheckResolver() {
        this.open = (handle, info, type, status, openAttribute, arguments) -> {
            if (status.getOpen().getAttribute() != InfoStatusOpenAttributeType.CLOSE) {
                throw new StatusAlreadyFinishedException();
            }
            if (openAttribute == InfoStatusOpenAttributeType.CLOSE
                    || (openAttribute == InfoStatusOpenAttributeType.OPEN_EXCLUSIVE && info.getOpened() > 0)
                    || (openAttribute == InfoStatusOpenAttributeType.OPEN_ONLY_READ && info.getOpened() > 0
                    && !type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.CAN_BE_SHARED_READ))
                    || (openAttribute == InfoStatusOpenAttributeType.OPEN_SHARED_WRITE
                    && !type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.CAN_BE_SHARED_WRITTEN))) {
                throw new StatusNotSupportedException();
            }

            return handle;
        };

        this.close = (info, type, status) -> {
            if (status.getOpen().getAttribute() == InfoStatusOpenAttributeType.CLOSE) {
                throw new StatusAlreadyFinishedException();
            }
        };

        this.createChildAndOpen = (childInfo, info, type, status, childType, identification) -> {
            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_CHILD)
                    || (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.CHILD_IS_NAMELESS) && identification.getType() == UUID.class)) {
                throw new StatusNotSupportedException();
            }
            Set<UUID> childTypes = type.getChildTypes();
            if (!childTypes.contains(UUIDUtil.getEmpty()) && !childTypes.contains(childType)) {
                throw new StatusNotSupportedException();
            }

            return childInfo;
        };

        this.getOrRebuildChild = (childInfo, info, type, status, identification, statusOpen) -> {
            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_CHILD)
                    || (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.CHILD_IS_NAMELESS) && identification.getType() == UUID.class)) {
                throw new StatusNotSupportedException();
            }

            return childInfo;
        };

        this.deleteChild = (info, type, status, identification) -> {
            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_CHILD)
                    || (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.CHILD_IS_NAMELESS) && identification.getType() == UUID.class)) {
                throw new StatusNotSupportedException();
            }
        };

        this.queryChild = (summaryDefinitions, info, type, status, queryChild) -> {
            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_CHILD)) {
                throw new StatusNotSupportedException();
            }

            return summaryDefinitions;
        };

        this.renameChild = (info, type, status, oldIdentification, newIdentification) -> {
            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_CHILD) || (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.CHILD_IS_NAMELESS))) {
                throw new StatusNotSupportedException();
            }
            if (oldIdentification.getType() == UUID.class || newIdentification.getType() == UUID.class) {
                throw new StatusNotSupportedException();
            }
        };

        this.readProperties = (properties, info, type, status) -> {
            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PROPERTIES)) {
                throw new StatusNotSupportedException();
            }

            return properties;
        };

        this.writeProperties = (info, type, status, properties) -> {
            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PROPERTIES)) {
                throw new StatusNotSupportedException();
            }
        };

        this.readContent = (content, info, type, status) -> {
            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_CONTENT)) {
                throw new StatusNotSupportedException();
            }
            if (status.getOpen().getAttribute() == InfoStatusOpenAttributeType.CLOSE) {
                throw new StatusRelationshipErrorException();
            }

            return content;
        };

        this.writeContent = (info, type, status, content) -> {
            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_CONTENT)) {
                throw new StatusNotSupportedException();
            }
            if (status.getOpen().getAttribute() == InfoStatusOpenAttributeType.CLOSE) {
                throw new StatusRelationshipErrorException();
            }
        };

        this.executeContent = (info, type, status) -> {
            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_CONTENT)
                    || !type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.CAN_BE_EXECUTED)) {
                throw new StatusNotSupportedException();
            }
            if (status.getOpen().getAttribute() == InfoStatusOpenAttributeType.CLOSE) {
                throw new StatusRelationshipErrorException();
            }
        };
    }

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

}
