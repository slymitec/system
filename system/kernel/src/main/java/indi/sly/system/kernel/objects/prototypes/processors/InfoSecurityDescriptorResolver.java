package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.lang.StatusDisabilityException;
import indi.sly.system.common.lang.StatusOverflowException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.objects.lang.*;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;
import indi.sly.system.kernel.security.values.AuditType;
import indi.sly.system.kernel.security.values.PermissionType;
import indi.sly.system.kernel.security.values.SecurityDescriptorSummaryDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoSecurityDescriptorResolver extends AInfoResolver {
    public InfoSecurityDescriptorResolver() {
        this.securityDescriptor = (info, type, status) -> {
            SecurityDescriptorObject securityDescriptor = this.factoryManager.create(SecurityDescriptorObject.class);

            if (!status.getIdentifications().isEmpty()) {
                List<IdentificationDefinition> identifications = new ArrayList<>(status.getIdentifications());
                identifications.remove(identifications.size() - 1);

                ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
                InfoObject parentInfo = objectManager.get(identifications);

                SecurityDescriptorObject parentSecurityDescriptor = null;
                try {
                    parentSecurityDescriptor = parentInfo.getSecurityDescriptor();
                } catch (StatusDisabilityException ignored) {
                }

                if (ObjectUtil.allNotNull(parentSecurityDescriptor)) {
                    securityDescriptor.setParent(parentSecurityDescriptor);
                }
            }

            securityDescriptor.setSource(info::getSecurityDescriptor, (source) -> {
                if (source.length > 4096) {
                    throw new StatusOverflowException();
                }

                info.setSecurityDescriptor(source);
            });

            AInfoTypeInitializer infoTypeInitializer = type.getInitializer();
            securityDescriptor.setLock((lock) -> infoTypeInitializer.lockProcedure(info, lock));

            securityDescriptor.setIdentifications(status.getIdentifications());
            securityDescriptor.setPermission(type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION));
            securityDescriptor.setAudit(type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT));

            return securityDescriptor;
        };

        this.dump = (dump, info, type, status) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.LISTCHILD_READDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.LISTCHILD_READDATA);
                }

                try {
                    List<SecurityDescriptorSummaryDefinition> securityDescriptorSummary = securityDescriptor.getSummary();
                    dump.getSecurityDescriptorSummary().addAll(securityDescriptorSummary);
                } catch (AKernelException ignored) {
                }
            }

            return dump;
        };

        this.open = (index, info, type, status, openAttribute, arguments) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.LISTCHILD_READDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.LISTCHILD_READDATA);
                }
            }

            return index;
        };

        this.createChild = (childInfo, info, type, status, childTypeID, identification) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.CREATECHILD_WRITEDATA);
                }
            }

            return childInfo;
        };

        this.getOrRebuildChild = (childInfo, info, type, status, identification, open) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.TRAVERSE_EXECUTE_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.TRAVERSE_EXECUTE);
                }
            }

            return childInfo;
        };

        this.deleteChild = (info, type, status, identification) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.DELETECHILD_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.DELETECHILD);
                }
            }
        };

        this.queryChild = (summaryDefinitions, info, type, status, wildcard) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.LISTCHILD_READDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.LISTCHILD_READDATA);
                }
            }

            return summaryDefinitions;
        };

        this.renameChild = (info, type, status, oldIdentification, newIdentification) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
                    securityDescriptor.checkPermission(PermissionType.DELETECHILD_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.CREATECHILD_WRITEDATA);
                    securityDescriptor.checkAudit(AuditType.DELETECHILD);
                }
            }
        };

        this.readProperties = (properties, info, type, status) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.READPROPERTIES_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.READPROPERTIES);
                }
            }

            return properties;
        };

        this.writeProperties = (info, type, status, properties) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.WRITEPROPERTIES_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.WRITEPROPERTIES);
                }
            }
        };

        this.readContent = (content, info, type, status) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.LISTCHILD_READDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.LISTCHILD_READDATA);
                }
            }

            return content;
        };

        this.writeContent = (info, type, status, content) -> {
            if ((type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT))
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.CREATECHILD_WRITEDATA);
                }
            }
        };

        this.executeContent = (info, type, status) -> {
            if ((type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT))
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.TRAVERSE_EXECUTE_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.TRAVERSE_EXECUTE);
                }
            }
        };
    }

    private final InfoProcessorSecurityDescriptorFunction securityDescriptor;
    private final InfoProcessorDumpFunction dump;
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
        if (ObjectUtil.allNotNull(info)) {
            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
            TypeObject type = typeManager.get(info.getType());

            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT) && !type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                return;
            }
        }

        processorMediator.setSecurityDescriptor(this.securityDescriptor);
        processorMediator.getDumps().add(this.dump);
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
        return 1;
    }
}
