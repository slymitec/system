package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.memory.caches.prototypes.InfoCacheObject;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.lang.*;
import indi.sly.system.kernel.objects.prototypes.wrappers.InfoProcessorMediator;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.security.values.SecurityDescriptorDefinition;
import indi.sly.system.kernel.security.prototypes.*;
import indi.sly.system.kernel.security.values.AccessControlTypes;
import indi.sly.system.kernel.security.values.AuditTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SecurityDescriptorResolver extends APrototype implements IInfoResolver {
    public SecurityDescriptorResolver() {
        this.securityDescriptor = (info, type, status) -> {
            SecurityDescriptorObject securityDescriptor = this.factoryManager.create(SecurityDescriptorObject.class);

            securityDescriptor.setSource(info::getSecurityDescriptor, info::setSecurityDescriptor);
            securityDescriptor.setLock((lockType) -> type.getTypeInitializer().lockProcedure(info, lockType));
            securityDescriptor.setIdentifications(status.getIdentifications());

            if (!ValueUtil.isAnyNullOrEmpty(status.getParentID())) {
                InfoCacheObject infoCache = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                        InfoCacheObject.class);

                InfoObject parentInfo = infoCache.getIfExisted(SpaceType.ALL, status.getParentID());

                try {
                    SecurityDescriptorObject parentSecurityDescriptor = parentInfo.getSecurityDescriptor();

                    if (ObjectUtil.allNotNull(parentSecurityDescriptor)) {
                        securityDescriptor.setParentSecurityDescriptor(parentSecurityDescriptor);
                    }
                } catch (StatusNotSupportedException ignored) {
                }
            }

            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                securityDescriptor.setPermission(true);
            } else {
                securityDescriptor.setPermission(false);
            }
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                securityDescriptor.setAudit(true);
            } else {
                securityDescriptor.setAudit(false);
            }

            return securityDescriptor;
        };

        this.dump = (dump, info, type, status) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT) || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.check(AccessControlTypes.LISTCHILD_READDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.LISTCHILD_READDATA);
                }
            }

            return dump;
        };

        this.open = (handle, info, type, status, openAttribute, arguments) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT) || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.check(AccessControlTypes.LISTCHILD_READDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.LISTCHILD_READDATA);
                }
            }

            return handle;
        };

        this.createChildAndOpen = (childInfo, info, type, status, childTypeID, identification) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT) || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.check(AccessControlTypes.CREATECHILD_WRITEDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.CREATECHILD_WRITEDATA);
                }
            }

            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
            TypeObject childType = typeManager.get(childTypeID);

            if (childType.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT) || childType.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorDefinition securityDescriptor = new SecurityDescriptorDefinition();

                if (childType.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
                    ProcessObject process = processManager.getCurrentProcess();

                    securityDescriptor.getOwners().add(process.getToken().getAccountID());
                    securityDescriptor.setInherit(true);
                    securityDescriptor.setHasChild(childType.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_CHILD));
                }

                childInfo.setSecurityDescriptor(ObjectUtil.transferToByteArray(securityDescriptor));
            } else {
                childInfo.setSecurityDescriptor(null);
            }

            return childInfo;
        };

        this.getOrRebuildChild = (childInfo, info, type, status, identification, statusOpen) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT) || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.check(AccessControlTypes.TRAVERSE_EXECUTE_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.TRAVERSE_EXECUTE);
                }
            }

            return childInfo;
        };

        this.deleteChild = (info, type, status, identification) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT) || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.check(AccessControlTypes.DELETECHILD_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.DELETECHILD);
                }
            }
        };

        this.queryChild = (summaryDefinitions, info, type, status, queryChild) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT) || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.check(AccessControlTypes.LISTCHILD_READDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.LISTCHILD_READDATA);
                }
            }

            return summaryDefinitions;
        };

        this.renameChild = (info, type, status, oldIdentification, newIdentification) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT) || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.check(LogicalUtil.or(AccessControlTypes.CREATECHILD_WRITEDATA_ALLOW, AccessControlTypes.DELETECHILD_ALLOW));
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(LogicalUtil.or(AuditTypes.CREATECHILD_WRITEDATA,
                            AuditTypes.DELETECHILD));
                }
            }
        };

        this.readProperties = (properties, info, type, status) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT) || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.check(AccessControlTypes.READPROPERTIES_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.READPROPERTIES);
                }
            }

            return properties;
        };

        this.writeProperties = (info, type, status, properties) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT) || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.check(AccessControlTypes.WRITEPROPERTIES_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.WRITEPROPERTIES);
                }
            }
        };

        this.readContent = (content, info, type, status) -> {
            if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT) || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.check(AccessControlTypes.LISTCHILD_READDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.LISTCHILD_READDATA);
                }
            }

            return content;
        };

        this.writeContent = (info, type, status, content) -> {
            if ((type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) || type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_PERMISSION)) {
                    securityDescriptor.check(AccessControlTypes.CREATECHILD_WRITEDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.CREATECHILD_WRITEDATA);
                }
            }
        };
    }

    private final SecurityDescriptorFunction securityDescriptor;
    private final DumpFunction dump;
    private final OpenFunction open;
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
        processorMediator.getCreateChildAndOpens().add(this.createChildAndOpen);
        processorMediator.getGetOrRebuildChilds().add(this.getOrRebuildChild);
        processorMediator.getDeleteChilds().add(this.deleteChild);
        processorMediator.getQueryChilds().add(this.queryChild);
        processorMediator.getRenameChilds().add(this.renameChild);
        processorMediator.getReadProperties().add(this.readProperties);
        processorMediator.getWriteProperties().add(this.writeProperties);
        processorMediator.getReadContents().add(this.readContent);
        processorMediator.getWriteContents().add(this.writeContent);
    }
}
