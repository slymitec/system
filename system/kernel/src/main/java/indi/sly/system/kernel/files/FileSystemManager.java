package indi.sly.system.kernel.files;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.files.instances.prototypes.wrappers.FileSystemFileTypeInitializer;
import indi.sly.system.kernel.files.instances.prototypes.wrappers.FileSystemFolderTypeInitializer;
import indi.sly.system.kernel.files.instances.prototypes.wrappers.FileSystemVolumeTypeInitializer;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileSystemManager extends AManager {
    @Override
    public void startup(long startup) {
        if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_SELF)) {
        } else if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_KERNEL)) {
            TypeManager typeManager = this.factoryManager.getManager(TypeManager.class);

            KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

            Set<UUID> childTypes = Set.of();

            typeManager.create(kernelConfiguration.FILES_TYPES_INSTANCE_FILE_ID,
                    kernelConfiguration.FILES_TYPES_INSTANCE_FILE_NAME,
                    LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_EXECUTED,
                            TypeInitializerAttributeType.CAN_BE_SENT_AND_INHERITED,
                            TypeInitializerAttributeType.CAN_BE_SHARED_READ,
                            TypeInitializerAttributeType.CAN_BE_SHARED_WRITTEN, TypeInitializerAttributeType.HAS_AUDIT,
                            TypeInitializerAttributeType.HAS_CONTENT, TypeInitializerAttributeType.HAS_PERMISSION,
                            TypeInitializerAttributeType.HAS_PROPERTIES),
                    childTypes, this.factoryManager.create(FileSystemFileTypeInitializer.class));

            childTypes = Set.of(kernelConfiguration.FILES_TYPES_INSTANCE_FOLDER_ID, kernelConfiguration.FILES_TYPES_INSTANCE_FILE_ID);

            typeManager.create(kernelConfiguration.FILES_TYPES_INSTANCE_FOLDER_ID,
                    kernelConfiguration.FILES_TYPES_INSTANCE_FOLDER_NAME,
                    LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_SENT_AND_INHERITED,
                            TypeInitializerAttributeType.CAN_BE_SHARED_READ,
                            TypeInitializerAttributeType.HAS_AUDIT, TypeInitializerAttributeType.HAS_CHILD,
                            TypeInitializerAttributeType.HAS_CONTENT, TypeInitializerAttributeType.HAS_PERMISSION,
                            TypeInitializerAttributeType.HAS_PROPERTIES),
                    childTypes, this.factoryManager.create(FileSystemFolderTypeInitializer.class));

            typeManager.create(kernelConfiguration.FILES_TYPES_INSTANCE_VOLUME_ID,
                    kernelConfiguration.FILES_TYPES_INSTANCE_VOLUME_NAME,
                    LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_SENT_AND_INHERITED,
                            TypeInitializerAttributeType.CAN_BE_SHARED_READ,
                            TypeInitializerAttributeType.HAS_AUDIT, TypeInitializerAttributeType.HAS_CHILD,
                            TypeInitializerAttributeType.HAS_CONTENT, TypeInitializerAttributeType.HAS_PERMISSION,
                            TypeInitializerAttributeType.HAS_PROPERTIES),
                    childTypes, this.factoryManager.create(FileSystemVolumeTypeInitializer.class));
        }
    }

    @Override
    public void shutdown() {
    }
}
