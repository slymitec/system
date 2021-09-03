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
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoEntity;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.objects.values.InfoSummaryDefinition;
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
            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);

            if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_KERNEL)) {
                AInfoRepositoryObject infoRepository = memoryManager.getInfoRepository(kernelConfiguration.MEMORY_REPOSITORIES_DATABASEENTITYREPOSITORYOBJECT_ID);

                if (!infoRepository.contain(kernelConfiguration.OBJECTS_PROTOTYPE_ROOT_ID)) {
                    TypeObject type = typeManager.get(kernelConfiguration.OBJECTS_TYPES_INSTANCE_FOLDER_ID);

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
                    securityDescriptor.setHasChild(type.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_CHILD));
                    AccessControlDefinition permission = new AccessControlDefinition();
                    permission.getUserID().setID(kernelConfiguration.SECURITY_GROUP_SYSTEMS_ID);
                    permission.getUserID().setType(UserType.GROUP);
                    permission.setScope(AccessControlScopeType.HIERARCHICAL_HAS_CHILD);
                    permission.setValue(PermissionType.FULLCONTROL_ALLOW);
                    securityDescriptor.getPermissions().add(permission);
                    permission = new AccessControlDefinition();
                    permission.getUserID().setID(kernelConfiguration.SECURITY_GROUP_ADMINISTRATORS_ID);
                    permission.getUserID().setType(UserType.GROUP);
                    permission.setScope(AccessControlScopeType.HIERARCHICAL_HAS_CHILD);
                    permission.setValue(LogicalUtil.or(PermissionType.LISTCHILD_READDATA_ALLOW,
                            PermissionType.TRAVERSE_EXECUTE_ALLOW, PermissionType.CREATECHILD_WRITEDATA_ALLOW,
                            PermissionType.READPROPERTIES_ALLOW, PermissionType.WRITEPROPERTIES_ALLOW,
                            PermissionType.READPERMISSIONDESCRIPTOR_ALLOW, PermissionType.CHANGEPERMISSIONDESCRIPTOR_ALLOW,
                            PermissionType.DELETECHILD_ALLOW));
                    securityDescriptor.getPermissions().add(permission);
                    permission = new AccessControlDefinition();
                    permission.getUserID().setID(kernelConfiguration.SECURITY_GROUP_USERS_ID);
                    permission.getUserID().setType(UserType.GROUP);
                    permission.setScope(AccessControlScopeType.HIERARCHICAL_HAS_CHILD);
                    permission.setValue(LogicalUtil.or(PermissionType.LISTCHILD_READDATA_ALLOW,
                            PermissionType.TRAVERSE_EXECUTE_ALLOW, PermissionType.READPROPERTIES_ALLOW,
                            PermissionType.READPERMISSIONDESCRIPTOR_ALLOW));
                    securityDescriptor.getPermissions().add(permission);
                    info.setSecurityDescriptor(ObjectUtil.transferToByteArray(securityDescriptor));

                    Map<String, String> childProperties = new HashMap<>();
                    info.setProperties(ObjectUtil.transferToByteArray(childProperties));

                    infoRepository.add(info);
                }
            } else if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_AFTER_KERNEL)) {
                InfoObject rootInfo = objectManager.get(List.of());
                Set<InfoSummaryDefinition> infoSummaryDefinitions = rootInfo.queryChild((InfoSummaryDefinition infoSummary) -> true);

                String[] childFolderNames = new String[]{"Files", "Ports", "Sessions", "Signals", "Audits"};
                UUID[] childFolderTypes = new UUID[]{kernelConfiguration.OBJECTS_TYPES_INSTANCE_FOLDER_ID,
                        kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID,
                        kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID,
                        kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID,
                        kernelConfiguration.OBJECTS_TYPES_INSTANCE_FOLDER_ID,};

                boolean isExist = false;
                for (int i = 0; i < childFolderNames.length; i++) {
                    for (InfoSummaryDefinition infoSummaryDefinition : infoSummaryDefinitions) {
                        if (childFolderNames[i].equals(infoSummaryDefinition.getName())) {
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
