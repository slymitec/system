package indi.sly.system.kernel.objects.prototypes.processors;

import indi.sly.system.common.exceptions.StatusNotSupportedException;
import indi.sly.system.common.functions.*;
import indi.sly.system.common.utility.LogicalUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.core.enviroment.SpaceTypes;
import indi.sly.system.kernel.memory.caches.prototypes.InfoObjectCacheObject;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.entities.InfoSummaryDefinition;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.prototypes.InfoObjectProcessorRegister;
import indi.sly.system.kernel.objects.prototypes.StatusDefinition;
import indi.sly.system.kernel.objects.prototypes.StatusOpenDefinition;
import indi.sly.system.kernel.objects.types.prototypes.TypeInitializerAttributeTypes;
import indi.sly.system.kernel.objects.types.prototypes.TypeObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.objects.prototypes.DumpDefinition;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.security.prototypes.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SecurityDescriptorProcessor extends ACoreObject implements IInfoObjectProcessor {
    public SecurityDescriptorProcessor() {
        this.securityDescriptor = (info, type, status) -> {
            SecurityDescriptorObject securityDescriptor = this.factoryManager.create(SecurityDescriptorObject.class);

            securityDescriptor.setSource(info::getSecurityDescriptor, info::setSecurityDescriptor);
            securityDescriptor.setLock((lockType) -> type.getTypeInitializer().lockProcedure(info, lockType));

            if (!UUIDUtils.isAnyNullOrEmpty(status.getParentID())) {
                InfoObjectCacheObject infoCacheObject =
                        this.factoryManager.getCoreObjectRepository().get(SpaceTypes.KERNEL,
                                InfoObjectCacheObject.class);

                InfoObject parentInfo = infoCacheObject.getIfExisted(SpaceTypes.ALL, status.getParentID());

                try {
                    securityDescriptor.setParentSecurityDescriptor(parentInfo.getSecurityDescriptor());
                } catch (StatusNotSupportedException ignored) {
                }
            }

            if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                securityDescriptor.setPermission(true);
            } else {
                securityDescriptor.setPermission(false);
            }
            if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT)) {
                securityDescriptor.setAudit(true);
            } else {
                securityDescriptor.setAudit(false);
            }

            securityDescriptor.setLock((lockType) -> type.getTypeInitializer().lockProcedure(info, lockType));

            return securityDescriptor;
        };

        this.dump = (dump, info, type, status) -> {
            if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT) || type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                    securityDescriptor.checkAccessControlType(AccessControlTypes.LISTCHILD_READDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.LISTCHILD_READDATA);
                }
            }

            return dump;
        };

        this.open = (handle, info, type, status, openAttribute, arguments) -> {
            if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT) || type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                    securityDescriptor.checkAccessControlType(AccessControlTypes.LISTCHILD_READDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.LISTCHILD_READDATA);
                }
            }

            return handle;
        };

        this.createChildAndOpen = (childInfo, info, type, status, childTypeID, identification) -> {
            if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT) || type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                    securityDescriptor.checkAccessControlType(AccessControlTypes.CREATECHILD_WRITEDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.CREATECHILD_WRITEDATA);
                }
            }

            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
            TypeObject childType = typeManager.get(childTypeID);

            if (childType.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT) || childType.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                SecurityDescriptorDefinition securityDescriptor = new SecurityDescriptorDefinition();

                if (childType.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                    ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
                    ProcessObject process = processManager.getCurrentProcess();

                    securityDescriptor.getOwners().add(process.getToken().getAccountID());
                    securityDescriptor.setInherit(true);
                }

                childInfo.setSecurityDescriptor(ObjectUtils.transferToByteArray(securityDescriptor));
            } else {
                childInfo.setSecurityDescriptor(null);
            }

            return childInfo;
        };

        this.getOrRebuildChild = (childInfo, info, type, status, identification, statusOpen) -> {
            if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT) || type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                    securityDescriptor.checkAccessControlType(AccessControlTypes.TRAVERSE_EXECUTE_ALLOW);
                }
                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.TRAVERSE_EXECUTE);
                }
            }

            return childInfo;
        };

        this.deleteChild = (info, type, status, identification) -> {
            if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT) || type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                    securityDescriptor.checkAccessControlType(AccessControlTypes.DELETECHILD_ALLOW);
                }
                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.DELETECHILD);
                }
            }
        };

        this.queryChild = (summaryDefinitions, info, type, status, queryChild) -> {
            if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT) || type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                    securityDescriptor.checkAccessControlType(AccessControlTypes.LISTCHILD_READDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.LISTCHILD_READDATA);
                }
            }

            return summaryDefinitions;
        };

        this.renameChild = (info, type, status, oldIdentification, newIdentification) -> {
            if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT) || type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                    securityDescriptor.checkAccessControlType(LogicalUtils.or(AccessControlTypes.CREATECHILD_WRITEDATA_ALLOW, AccessControlTypes.DELETECHILD_ALLOW));
                }
                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(LogicalUtils.or(AuditTypes.CREATECHILD_WRITEDATA,
                            AuditTypes.DELETECHILD));
                }
            }
        };

        this.readProperties = (properties, info, type, status) -> {
            if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT) || type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                    securityDescriptor.checkAccessControlType(AccessControlTypes.READPROPERTIES_ALLOW);
                }
                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.READPROPERTIES);
                }
            }

            return properties;
        };

        this.writeProperties = (info, type, status, properties) -> {
            if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT) || type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                    securityDescriptor.checkAccessControlType(AccessControlTypes.WRITEPROPERTIES_ALLOW);
                }
                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.WRITEPROPERTIES);
                }
            }
        };

        this.readContent = (content, info, type, status) -> {
            if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT) || type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                    securityDescriptor.checkAccessControlType(AccessControlTypes.LISTCHILD_READDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.LISTCHILD_READDATA);
                }
            }

            return content;
        };

        this.writeContent = (info, type, status, content) -> {
            if ((type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT)) || type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                SecurityDescriptorObject securityDescriptor = this.securityDescriptor.apply(info, type, status);

                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                    securityDescriptor.checkAccessControlType(AccessControlTypes.CREATECHILD_WRITEDATA_ALLOW);
                }
                if (type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT)) {
                    securityDescriptor.writeAudit(AuditTypes.CREATECHILD_WRITEDATA);
                }
            }
        };
    }

    private final Function3<SecurityDescriptorObject, InfoEntity, TypeObject, StatusDefinition> securityDescriptor;
    private final Function4<DumpDefinition, DumpDefinition, InfoEntity, TypeObject, StatusDefinition> dump;
    private final Function6<UUID, UUID, InfoEntity, TypeObject, StatusDefinition, Long, Object[]> open;
    private final Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, StatusDefinition, UUID, Identification> createChildAndOpen;
    private final Function6<InfoEntity, InfoEntity, InfoEntity, TypeObject, StatusDefinition, Identification,
            StatusOpenDefinition> getOrRebuildChild;
    private final Consumer4<InfoEntity, TypeObject, StatusDefinition, Identification> deleteChild;
    private final Function5<Set<InfoSummaryDefinition>, Set<InfoSummaryDefinition>, InfoEntity, TypeObject,
            StatusDefinition, Predicate<InfoSummaryDefinition>> queryChild;
    private final Consumer5<InfoEntity, TypeObject, StatusDefinition, Identification, Identification> renameChild;
    private final Function4<Map<String, String>, Map<String, String>, InfoEntity, TypeObject, StatusDefinition> readProperties;
    private final Consumer4<InfoEntity, TypeObject, StatusDefinition, Map<String, String>> writeProperties;
    private final Function4<byte[], byte[], InfoEntity, TypeObject, StatusDefinition> readContent;
    private final Consumer4<InfoEntity, TypeObject, StatusDefinition, byte[]> writeContent;

    @Override
    public void process(InfoEntity info, InfoObjectProcessorRegister processorRegister) {
        if (ObjectUtils.allNotNull(info)) {
            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);
            TypeObject type = typeManager.get(info.getType());

            if (!type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_AUDIT) && !type.isTypeInitializerAttributeExist(TypeInitializerAttributeTypes.HAS_PERMISSION)) {
                return;
            }
        }

        processorRegister.setSecurityDescriptor(this.securityDescriptor);
        processorRegister.getDumps().add(this.dump);
        processorRegister.getOpens().add(this.open);
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
