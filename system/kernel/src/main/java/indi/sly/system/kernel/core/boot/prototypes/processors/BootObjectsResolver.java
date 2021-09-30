package indi.sly.system.kernel.core.boot.prototypes.processors;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.boot.lang.BootStartConsumer;
import indi.sly.system.kernel.core.boot.prototypes.wrappers.BootProcessorMediator;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
import indi.sly.system.kernel.security.prototypes.SecurityDescriptorObject;
import indi.sly.system.kernel.security.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BootObjectsResolver extends ABootResolver {
    public BootObjectsResolver() {
        this.start = (startup) -> {
            KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

            MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
            ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

            if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_KERNEL)) {
                AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(kernelConfiguration.MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID);

                if (!infoRepository.contain(kernelConfiguration.OBJECTS_PROTOTYPE_ROOT_ID)) {
                    InfoEntity info = new InfoEntity();

                    info.setID(kernelConfiguration.OBJECTS_PROTOTYPE_ROOT_ID);
                    info.setType(kernelConfiguration.OBJECTS_TYPES_INSTANCE_FOLDER_ID);
                    info.setOpened(0L);

                    DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
                    long nowDateTime = dateTime.getCurrentDateTime();

                    Map<Long, Long> date = new HashMap<>();
                    date.put(DateTimeType.CREATE, nowDateTime);
                    date.put(DateTimeType.MODIFIED, nowDateTime);
                    date.put(DateTimeType.ACCESS, nowDateTime);
                    info.setDate(ObjectUtil.transferToByteArray(date));

                    SecurityDescriptorDefinition securityDescriptor = new SecurityDescriptorDefinition();
                    securityDescriptor.getOwners().add(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_ID);
                    securityDescriptor.setInherit(false);
                    securityDescriptor.setHasChild(true);
                    AccessControlDefinition permission = new AccessControlDefinition();
                    permission.getUserID().setID(kernelConfiguration.SECURITY_GROUP_SYSTEMS_ID);
                    permission.getUserID().setType(UserType.GROUP);
                    permission.setScope(AccessControlScopeType.ALL);
                    permission.setValue(PermissionType.FULLCONTROL_ALLOW);
                    securityDescriptor.getPermissions().add(permission);
                    permission = new AccessControlDefinition();
                    permission.getUserID().setID(kernelConfiguration.SECURITY_GROUP_ADMINISTRATORS_ID);
                    permission.getUserID().setType(UserType.GROUP);
                    permission.setScope(AccessControlScopeType.ALL);
                    permission.setValue(LogicalUtil.or(PermissionType.LISTCHILD_READDATA_ALLOW,
                            PermissionType.TRAVERSE_EXECUTE_ALLOW, PermissionType.CREATECHILD_WRITEDATA_ALLOW,
                            PermissionType.READPROPERTIES_ALLOW, PermissionType.READPERMISSIONDESCRIPTOR_ALLOW,
                            PermissionType.DELETECHILD_ALLOW));
                    securityDescriptor.getPermissions().add(permission);
                    permission = new AccessControlDefinition();
                    permission.getUserID().setID(kernelConfiguration.SECURITY_GROUP_USERS_ID);
                    permission.getUserID().setType(UserType.GROUP);
                    permission.setScope(AccessControlScopeType.ALL);
                    permission.setValue(LogicalUtil.or(PermissionType.LISTCHILD_READDATA_ALLOW,
                            PermissionType.TRAVERSE_EXECUTE_ALLOW, PermissionType.READPROPERTIES_ALLOW));
                    securityDescriptor.getPermissions().add(permission);
                    info.setSecurityDescriptor(ObjectUtil.transferToByteArray(securityDescriptor));

                    Map<String, String> childProperties = new HashMap<>();
                    info.setProperties(ObjectUtil.transferToByteArray(childProperties));

                    infoRepository.add(info);
                }
            } else if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_AFTER_KERNEL)) {
                InfoObject rootInfo = objectManager.get(List.of());
                Set<InfoSummaryDefinition> infoSummaries = rootInfo.queryChild((InfoSummaryDefinition infoSummary) -> true);

                String[] childFolderNames = new String[]{"Audits", "Files", "Sessions"};
                UUID[] childFolderTypes = new UUID[]{kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID,
                        kernelConfiguration.OBJECTS_TYPES_INSTANCE_FOLDER_ID,
                        kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID};

                boolean isExist = false;
                for (int i = 0; i < childFolderNames.length; i++) {
                    for (InfoSummaryDefinition infoSummary : infoSummaries) {
                        if (childFolderNames[i].equals(infoSummary.getName())) {
                            isExist = true;
                            break;
                        }
                    }

                    if (!isExist) {
                        InfoObject childInfo = rootInfo.createChildAndOpen(childFolderTypes[i],
                                new IdentificationDefinition(childFolderNames[i]), InfoOpenAttributeType.OPEN_EXCLUSIVE);

                        childInfo.close();
                    }

                    isExist = false;
                }

                childFolderNames = new String[]{"Ports", "Signals"};
                childFolderTypes = new UUID[]{kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID,
                        kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID};

                isExist = false;
                for (int i = 0; i < childFolderNames.length; i++) {
                    for (InfoSummaryDefinition infoSummary : infoSummaries) {
                        if (childFolderNames[i].equals(infoSummary.getName())) {
                            isExist = true;
                            break;
                        }
                    }

                    if (!isExist) {
                        InfoObject childInfo = rootInfo.createChildAndOpen(childFolderTypes[i],
                                new IdentificationDefinition(childFolderNames[i]), InfoOpenAttributeType.OPEN_EXCLUSIVE);

                        SecurityDescriptorObject auditSecurityDescriptor = childInfo.getSecurityDescriptor();
                        Set<AccessControlDefinition> permissions = new HashSet<>();
                        AccessControlDefinition permission = new AccessControlDefinition();
                        permission.getUserID().setID(kernelConfiguration.SECURITY_GROUP_USERS_ID);
                        permission.getUserID().setType(UserType.GROUP);
                        permission.setScope(AccessControlScopeType.ALL);
                        permission.setValue(PermissionType.DELETECHILD_ALLOW);
                        permissions.add(permission);
                        auditSecurityDescriptor.setPermissions(permissions);

                        childInfo.close();
                    }

                    isExist = false;
                }

                InfoObject parentInfo = rootInfo.getChild(new IdentificationDefinition("Sessions"));
                infoSummaries = parentInfo.queryChild(infoSummary -> kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_ID.equals(infoSummary.getID()));
                if (infoSummaries.isEmpty()) {
                    InfoObject childInfo = parentInfo.createChildAndOpen(kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID,
                            new IdentificationDefinition(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_ID), InfoOpenAttributeType.OPEN_EXCLUSIVE);

                    SecurityDescriptorObject auditSecurityDescriptor = childInfo.getSecurityDescriptor();
                    Set<AccessControlDefinition> permissions = new HashSet<>();
                    AccessControlDefinition permission = new AccessControlDefinition();
                    permission.getUserID().setID(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_ID);
                    permission.getUserID().setType(UserType.ACCOUNT);
                    permission.setScope(AccessControlScopeType.ALL);
                    permission.setValue(LogicalUtil.or(PermissionType.LISTCHILD_READDATA_ALLOW,
                            PermissionType.TRAVERSE_EXECUTE_ALLOW, PermissionType.CREATECHILD_WRITEDATA_ALLOW,
                            PermissionType.READPROPERTIES_ALLOW, PermissionType.WRITEPROPERTIES_ALLOW,
                            PermissionType.READPERMISSIONDESCRIPTOR_ALLOW, PermissionType.DELETECHILD_ALLOW));
                    permissions.add(permission);
                    permission.getUserID().setID(kernelConfiguration.SECURITY_GROUP_USERS_ID);
                    permission.getUserID().setType(UserType.GROUP);
                    permission.setScope(AccessControlScopeType.ALL);
                    permission.setValue(PermissionType.DELETECHILD_ALLOW);
                    permissions.add(permission);
                    auditSecurityDescriptor.setPermissions(permissions);
                    auditSecurityDescriptor.setInherit(true);

                    childInfo.close();
                }

                parentInfo = rootInfo.getChild(new IdentificationDefinition("Audits"));
                infoSummaries = parentInfo.queryChild(infoSummary -> kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_ID.equals(infoSummary.getID()));
                if (infoSummaries.isEmpty()) {
                    InfoObject childInfo = parentInfo.createChildAndOpen(kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID,
                            new IdentificationDefinition(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_ID), InfoOpenAttributeType.OPEN_EXCLUSIVE);

                    SecurityDescriptorObject auditSecurityDescriptor = childInfo.getSecurityDescriptor();
                    Set<AccessControlDefinition> permissions = new HashSet<>();
                    AccessControlDefinition permission = new AccessControlDefinition();
                    permission.getUserID().setID(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_ID);
                    permission.getUserID().setType(UserType.ACCOUNT);
                    permission.setScope(AccessControlScopeType.ALL);
                    permission.setValue(LogicalUtil.or(PermissionType.LISTCHILD_READDATA_ALLOW,
                            PermissionType.TRAVERSE_EXECUTE_ALLOW, PermissionType.CREATECHILD_WRITEDATA_ALLOW,
                            PermissionType.READPROPERTIES_ALLOW, PermissionType.WRITEPROPERTIES_ALLOW,
                            PermissionType.READPERMISSIONDESCRIPTOR_ALLOW, PermissionType.DELETECHILD_ALLOW));
                    permissions.add(permission);
                    auditSecurityDescriptor.setPermissions(permissions);
                    auditSecurityDescriptor.setInherit(false);

                    childInfo.close();
                }

                parentInfo = rootInfo.getChild(new IdentificationDefinition("Files"));
                infoSummaries = parentInfo.queryChild(infoSummary -> "Main".equals(infoSummary.getName()));
                if (infoSummaries.isEmpty()) {
                    InfoObject childInfo = parentInfo.createChildAndOpen(kernelConfiguration.FILES_TYPES_INSTANCE_FOLDER_ID,
                            new IdentificationDefinition("Main"), InfoOpenAttributeType.OPEN_EXCLUSIVE);

                    childInfo.close();
                }
                parentInfo = parentInfo.getChild(new IdentificationDefinition("Main"));
                infoSummaries = parentInfo.queryChild(infoSummary -> "Home".equals(infoSummary.getName()));
                if (infoSummaries.isEmpty()) {
                    InfoObject childInfo = parentInfo.createChildAndOpen(kernelConfiguration.FILES_TYPES_INSTANCE_FOLDER_ID,
                            new IdentificationDefinition("Home"), InfoOpenAttributeType.OPEN_EXCLUSIVE);

                    childInfo.close();
                }
            }
        };
    }

    private final BootStartConsumer start;

    @Override
    public void resolve(BootProcessorMediator processorMediator) {
        processorMediator.getStarts().add(this.start);
    }

    @Override
    public int order() {
        return 1;
    }
}
