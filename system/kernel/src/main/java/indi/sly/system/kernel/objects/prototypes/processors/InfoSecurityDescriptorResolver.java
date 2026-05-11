package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.objects.lang.*;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;
import indi.sly.system.kernel.security.values.AuditType;
import indi.sly.system.kernel.security.values.PermissionType;
import indi.sly.system.kernel.security.values.SecurityDescriptorCacheEntity;
import indi.sly.system.kernel.security.values.SecurityDescriptorSummaryDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InfoSecurityDescriptorResolver extends AResolver implements IInfoResolver {
    public InfoSecurityDescriptorResolver() {
        this.securityDescriptor = (info, type, cache) -> {
            SecurityDescriptorCacheEntity securityDescriptorCache = new SecurityDescriptorCacheEntity();

            securityDescriptorCache.setPermission(type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION));
            securityDescriptorCache.setAudit(type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT));

            return securityDescriptorCache;
        };

        this.dump = (dump, info, type, cache) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorCacheEntity securityDescriptorCache = this.securityDescriptor.apply(info, type, cache);

                ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
                SecurityDescriptorObject securityDescriptor = objectManager.getFactory().rebuildSecurityDescriptor(securityDescriptorCache);

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

        this.open = (index, info, type, cache, openAttribute, arguments) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorCacheEntity securityDescriptorCache = this.securityDescriptor.apply(info, type, cache);

                ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
                SecurityDescriptorObject securityDescriptor = objectManager.getFactory().rebuildSecurityDescriptor(securityDescriptorCache);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.LISTCHILD_READDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.LISTCHILD_READDATA);
                }
            }

            return index;
        };

        this.createChild = (childInfo, info, type, cache, childTypeID, identification) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorCacheEntity securityDescriptorCache = this.securityDescriptor.apply(info, type, cache);

                ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
                SecurityDescriptorObject securityDescriptor = objectManager.getFactory().rebuildSecurityDescriptor(securityDescriptorCache);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.CREATECHILD_WRITEDATA);
                }
            }

            return childInfo;
        };

        this.getChild = (childInfo, info, type, cache, identification) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorCacheEntity securityDescriptorCache = this.securityDescriptor.apply(info, type, cache);

                ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
                SecurityDescriptorObject securityDescriptor = objectManager.getFactory().rebuildSecurityDescriptor(securityDescriptorCache);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.TRAVERSE_EXECUTE_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.TRAVERSE_EXECUTE);
                }
            }

            return childInfo;
        };

        this.deleteChild = (info, type, cache, identification) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorCacheEntity securityDescriptorCache = this.securityDescriptor.apply(info, type, cache);

                ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
                SecurityDescriptorObject securityDescriptor = objectManager.getFactory().rebuildSecurityDescriptor(securityDescriptorCache);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.DELETECHILD_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.DELETECHILD);
                }
            }
        };

        this.queryChild = (summaryDefinitions, info, type, cache, wildcard) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorCacheEntity securityDescriptorCache = this.securityDescriptor.apply(info, type, cache);

                ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
                SecurityDescriptorObject securityDescriptor = objectManager.getFactory().rebuildSecurityDescriptor(securityDescriptorCache);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.LISTCHILD_READDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.LISTCHILD_READDATA);
                }
            }

            return summaryDefinitions;
        };

        this.renameChild = (info, type, cache, oldIdentification, newIdentification) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorCacheEntity securityDescriptorCache = this.securityDescriptor.apply(info, type, cache);

                ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
                SecurityDescriptorObject securityDescriptor = objectManager.getFactory().rebuildSecurityDescriptor(securityDescriptorCache);

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

        this.readProperties = (properties, info, type, cache) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorCacheEntity securityDescriptorCache = this.securityDescriptor.apply(info, type, cache);

                ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
                SecurityDescriptorObject securityDescriptor = objectManager.getFactory().rebuildSecurityDescriptor(securityDescriptorCache);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.READPROPERTIES_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.READPROPERTIES);
                }
            }

            return properties;
        };

        this.writeProperties = (info, type, cache, properties) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorCacheEntity securityDescriptorCache = this.securityDescriptor.apply(info, type, cache);

                ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
                SecurityDescriptorObject securityDescriptor = objectManager.getFactory().rebuildSecurityDescriptor(securityDescriptorCache);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.WRITEPROPERTIES_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.WRITEPROPERTIES);
                }
            }
        };

        this.readContent = (content, info, type, cache) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorCacheEntity securityDescriptorCache = this.securityDescriptor.apply(info, type, cache);

                ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
                SecurityDescriptorObject securityDescriptor = objectManager.getFactory().rebuildSecurityDescriptor(securityDescriptorCache);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.LISTCHILD_READDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.LISTCHILD_READDATA);
                }
            }

            return content;
        };

        this.writeContent = (info, type, cache, content) -> {
            if ((type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT))
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorCacheEntity securityDescriptorCache = this.securityDescriptor.apply(info, type, cache);

                ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
                SecurityDescriptorObject securityDescriptor = objectManager.getFactory().rebuildSecurityDescriptor(securityDescriptorCache);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.checkPermission(PermissionType.CREATECHILD_WRITEDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.checkAudit(AuditType.CREATECHILD_WRITEDATA);
                }
            }
        };

        this.executeContent = (info, type, cache) -> {
            if ((type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT))
                    || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorCacheEntity securityDescriptorCache = this.securityDescriptor.apply(info, type, cache);

                ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);
                SecurityDescriptorObject securityDescriptor = objectManager.getFactory().rebuildSecurityDescriptor(securityDescriptorCache);

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
        if (ObjectUtil.allNotNull(info)) {
            TypeManager typeManager = this.coreManager.getManager(TypeManager.class);
            TypeObject type = typeManager.get(info.getType());

            if (!type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT) && !type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                return;
            }
        }

        processorMediator.setSecurityDescriptor(this.securityDescriptor);
        processorMediator.getDumps().add(this.dump);
        processorMediator.getOpens().add(this.open);
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
        return 1;
    }
}
