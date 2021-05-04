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
                    && !type.isTypeInitializerAttributeExist(TypeInitializerAttributeType.CAN_BE_SHARED_READ))
                    || (openAttribute == InfoStatusOpenAttributeType.OPEN_SHARED_WRITE
                    && !type.isTypeInitializerAttributeExist(TypeInitializerAttributeType.CAN_BE_SHARED_WRITE))) {
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
            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeType.HAS_CHILD)
                    || (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeType.CHILD_IS_NAMELESS) && identification.getType() == UUID.class)) {
                throw new StatusNotSupportedException();
            }
            Set<UUID> childTypes = type.getChildTypes();
            if (!childTypes.contains(UUIDUtil.getEmpty()) && !childTypes.contains(childType)) {
                throw new StatusNotSupportedException();
            }

            return childInfo;
        };

        this.getOrRebuildChild = (childInfo, info, type, status, identification, statusOpen) -> {
            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeType.HAS_CHILD)
                    || (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeType.CHILD_IS_NAMELESS) && identification.getType() == UUID.class)) {
                throw new StatusNotSupportedException();
            }

            return childInfo;
        };

        this.deleteChild = (info, type, status, identification) -> {
            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeType.HAS_CHILD)
                    || (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeType.CHILD_IS_NAMELESS) && identification.getType() == UUID.class)) {
                throw new StatusNotSupportedException();
            }
        };

        this.queryChild = (summaryDefinitions, info, type, status, queryChild) -> {
            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeType.HAS_CHILD)) {
                throw new StatusNotSupportedException();
            }

            return summaryDefinitions;
        };

        this.renameChild = (info, type, status, oldIdentification, newIdentification) -> {
            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeType.HAS_CHILD) || (type.isTypeInitializerAttributeExist(TypeInitializerAttributeType.CHILD_IS_NAMELESS))) {
                throw new StatusNotSupportedException();
            }
            if (oldIdentification.getType() == UUID.class || newIdentification.getType() == UUID.class) {
                throw new StatusNotSupportedException();
            }
        };

        this.readProperties = (properties, info, type, status) -> {
            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeType.HAS_PROPERTIES)) {
                throw new StatusNotSupportedException();
            }

            return properties;
        };

        this.writeProperties = (info, type, status, properties) -> {
            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeType.HAS_PROPERTIES)) {
                throw new StatusNotSupportedException();
            }
        };

        this.readContent = (content, info, type, status) -> {
            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeType.HAS_CONTENT)) {
                throw new StatusNotSupportedException();
            }
            if (status.getOpen().getAttribute() == InfoStatusOpenAttributeType.CLOSE) {
                throw new StatusRelationshipErrorException();
            }

            return content;
        };

        this.writeContent = (info, type, status, content) -> {
            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeType.HAS_CONTENT)) {
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

    @Override
    public void resolve(InfoEntity info, InfoProcessorMediator processorRegister) {
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
