package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.objects.lang.*;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoCheckConditionResolver extends AInfoResolver {
    public InfoCheckConditionResolver() {
        this.open = (index, info, type, status, openAttribute, arguments) -> {
            if (LogicalUtil.isAnyEqual(openAttribute, InfoOpenAttributeType.CLOSE)
                    || (LogicalUtil.isAnyEqual(openAttribute, InfoOpenAttributeType.OPEN_EXCLUSIVE) && info.getOpened() > 0)
                    || (LogicalUtil.isAnyEqual(openAttribute, InfoOpenAttributeType.OPEN_ONLY_READ) && info.getOpened() > 0
                    && !type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.CAN_BE_SHARED_READ))
                    || (LogicalUtil.isAnyEqual(openAttribute, InfoOpenAttributeType.OPEN_SHARED_WRITE)
                    && !type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.CAN_BE_SHARED_WRITTEN))) {
                throw new StatusNotSupportedException();
            }

            if (status.getIdentifications().isEmpty()) {
                throw new StatusNotSupportedException();
            }

            return index;
        };

        this.createChild = (childInfo, info, type, status, childType, identification) -> {
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

        this.getOrRebuildChild = (childInfo, info, type, status, identification, open) -> {
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

        this.queryChild = (summaryDefinitions, info, type, status, wildcard) -> {
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

            return content;
        };

        this.writeContent = (info, type, status, content) -> {
            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_CONTENT)) {
                throw new StatusNotSupportedException();
            }
        };

        this.executeContent = (info, type, status) -> {
            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_CONTENT)
                    || !type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.CAN_BE_EXECUTED)) {
                throw new StatusNotSupportedException();
            }
        };
    }

    private final InfoProcessorOpenFunction open;
    private final InfoProcessorCreateChildFunction createChild;
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
        processorMediator.getOpens().add(this.open);
        processorMediator.getCreateChilds().add(this.createChild);
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
        return 0;
    }
}
