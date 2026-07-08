package indi.sly.system.kernel.core.boot.prototypes.processors;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.values.IdentifierRecord;
import indi.sly.system.common.values.PathRecord;
import indi.sly.system.kernel.core.boot.lang.BootStartConsumer;
import indi.sly.system.kernel.core.boot.prototypes.mediators.BootProcessorMediator;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.AInfoRepositoryObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoWildcardRecord;
import indi.sly.system.kernel.objects.values.InfoSummaryRecord;
import indi.sly.system.kernel.objects.prototypes.SecurityDescriptorObject;
import indi.sly.system.kernel.security.values.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BootObjectsResolver extends ABootResolver {
    public BootObjectsResolver() {
        this.start = (startup) -> {
            KernelConfigurationDefinition kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

            MemoryManager memoryManager = this.coreManager.getManager(MemoryManager.class);
            ObjectManager objectManager = this.coreManager.getManager(ObjectManager.class);

            if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_KERNEL)) {
                AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(kernelConfiguration.MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORY_ID);

                if (!infoRepository.contain(kernelConfiguration.OBJECTS_PROTOTYPE_ROOT_ID)) {
                    InfoEntity info = new InfoEntity();

                    info.setId(kernelConfiguration.OBJECTS_PROTOTYPE_ROOT_ID);
                    info.setType(kernelConfiguration.OBJECTS_TYPES_INSTANCE_FOLDER_ID);
                    info.setOpened(0L);

                    DateTimeObject dateTime = this.coreManager.getDateTime();
                    long nowDateTime = dateTime.getCurrent();

                    Map<Long, Long> date = new HashMap<>();
                    date.put(DateTimeType.CREATE, nowDateTime);
                    date.put(DateTimeType.MODIFIED, nowDateTime);
                    date.put(DateTimeType.ACCESS, nowDateTime);
                    info.setDate(date);

                    SecurityDescriptorEntity securityDescriptor = new SecurityDescriptorEntity();
                    securityDescriptor.getOwners().add(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_ID);
                    securityDescriptor.setInherit(false);
                    securityDescriptor.setHasChild(true);
                    AccessControlRecord permission = new AccessControlRecord(new UserIdRecord(kernelConfiguration.SECURITY_GROUP_SYSTEMS_ID, UserType.GROUP), AccessControlScopeType.ALL, PermissionType.FULLCONTROL_ALLOW);
                    securityDescriptor.getPermissions().add(permission);
                    permission = new AccessControlRecord(new UserIdRecord(kernelConfiguration.SECURITY_GROUP_ADMINISTRATORS_ID, UserType.GROUP), AccessControlScopeType.ALL, LogicalUtil.or(PermissionType.LISTCHILD_READDATA_ALLOW, PermissionType.TRAVERSE_EXECUTE_ALLOW, PermissionType.CREATECHILD_WRITEDATA_ALLOW, PermissionType.READPROPERTIES_ALLOW, PermissionType.READPERMISSIONDESCRIPTOR_ALLOW, PermissionType.DELETECHILD_ALLOW));
                    securityDescriptor.getPermissions().add(permission);
                    permission = new AccessControlRecord(new UserIdRecord(kernelConfiguration.SECURITY_GROUP_USERS_ID, UserType.GROUP), AccessControlScopeType.ALL, LogicalUtil.or(PermissionType.LISTCHILD_READDATA_ALLOW, PermissionType.TRAVERSE_EXECUTE_ALLOW, PermissionType.READPROPERTIES_ALLOW));
                    securityDescriptor.getPermissions().add(permission);
                    info.setSecurityDescriptor(securityDescriptor);

                    Map<String, String> properties = new HashMap<>();
                    info.setProperties(properties);

                    infoRepository.add(info);
                }
            } else if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_AFTER_KERNEL)) {
                InfoObject rootInfo = objectManager.get(new PathRecord(List.of()));
                Set<InfoSummaryRecord> infoSummaries;

                String[] childFolderNames = new String[]{"Audits", "Files", "Sessions", "Services"};
                UUID[] childFolderTypes = new UUID[]{kernelConfiguration.OBJECTS_TYPES_INSTANCE_FOLDER_ID, kernelConfiguration.OBJECTS_TYPES_INSTANCE_FOLDER_ID, kernelConfiguration.OBJECTS_TYPES_INSTANCE_FOLDER_ID, kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID};

                for (int i = 0; i < childFolderNames.length; i++) {
                    infoSummaries = rootInfo.queryChild(new InfoWildcardRecord(childFolderNames[i]));

                    if (infoSummaries.isEmpty()) {
                        rootInfo.createChild(childFolderTypes[i], new IdentifierRecord(childFolderNames[i]));
                    }
                }

                InfoObject parentInfo = rootInfo.getChild(new IdentifierRecord("Sessions"));
                InfoWildcardRecord wildcard = new InfoWildcardRecord(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_NAME);
                infoSummaries = parentInfo.queryChild(wildcard);
                if (infoSummaries.isEmpty()) {
                    InfoObject childInfo = parentInfo.createChild(kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID, new IdentifierRecord(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_NAME));

                    SecurityDescriptorObject auditSecurityDescriptor = childInfo.getSecurityDescriptor();
                    Set<AccessControlRecord> permissions = new HashSet<>();
                    AccessControlRecord permission = new AccessControlRecord(
                            new UserIdRecord(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_ID, UserType.ACCOUNT),
                            AccessControlScopeType.ALL,
                            LogicalUtil.or(PermissionType.LISTCHILD_READDATA_ALLOW, PermissionType.TRAVERSE_EXECUTE_ALLOW, PermissionType.CREATECHILD_WRITEDATA_ALLOW, PermissionType.READPROPERTIES_ALLOW, PermissionType.WRITEPROPERTIES_ALLOW, PermissionType.READPERMISSIONDESCRIPTOR_ALLOW, PermissionType.DELETECHILD_ALLOW));
                    permissions.add(permission);
                    permission = new AccessControlRecord(new UserIdRecord(kernelConfiguration.SECURITY_GROUP_USERS_ID, UserType.GROUP), AccessControlScopeType.ALL, PermissionType.DELETECHILD_ALLOW);
                    permissions.add(permission);
                    auditSecurityDescriptor.setPermissions(permissions);
                    auditSecurityDescriptor.setInherit(true);
                }

                parentInfo = rootInfo.getChild(new IdentifierRecord("Audits"));
                infoSummaries = parentInfo.queryChild(wildcard);
                if (infoSummaries.isEmpty()) {
                    InfoObject childInfo = parentInfo.createChild(kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID, new IdentifierRecord(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_NAME));

                    SecurityDescriptorObject auditSecurityDescriptor = childInfo.getSecurityDescriptor();
                    Set<AccessControlRecord> permissions = new HashSet<>();
                    AccessControlRecord permission = new AccessControlRecord(
                            new UserIdRecord(kernelConfiguration.SECURITY_ACCOUNT_SYSTEM_ID, UserType.ACCOUNT),
                            AccessControlScopeType.ALL,
                            LogicalUtil.or(PermissionType.LISTCHILD_READDATA_ALLOW, PermissionType.TRAVERSE_EXECUTE_ALLOW, PermissionType.CREATECHILD_WRITEDATA_ALLOW, PermissionType.READPROPERTIES_ALLOW, PermissionType.WRITEPROPERTIES_ALLOW, PermissionType.READPERMISSIONDESCRIPTOR_ALLOW, PermissionType.DELETECHILD_ALLOW));
                    permissions.add(permission);
                    auditSecurityDescriptor.setPermissions(permissions);
                    auditSecurityDescriptor.setInherit(false);
                }

                parentInfo = rootInfo.getChild(new IdentifierRecord("Files"));
                wildcard = new InfoWildcardRecord("Main");
                infoSummaries = parentInfo.queryChild(wildcard);
                if (infoSummaries.isEmpty()) {
                    parentInfo.createChild(kernelConfiguration.FILES_TYPES_INSTANCE_FOLDER_ID, new IdentifierRecord("Main"));
                }
                parentInfo = parentInfo.getChild(new IdentifierRecord("Main"));
                wildcard = new InfoWildcardRecord("Home");
                infoSummaries = parentInfo.queryChild(wildcard);
                if (infoSummaries.isEmpty()) {
                    parentInfo.createChild(kernelConfiguration.FILES_TYPES_INSTANCE_FOLDER_ID, new IdentifierRecord("Home"));
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
