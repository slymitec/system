package indi.sly.system.kernel.files;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.environment.containers.KernelConfiguration;
import indi.sly.system.kernel.files.instances.prototypes.processors.FileSystemFileTypeInitializer;
import indi.sly.system.kernel.files.instances.prototypes.processors.FileSystemFolderTypeInitializer;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileSystemManager extends AManager {
    @Override
    public void startup(long startup) {
        if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_KERNEL)) {
            TypeManager typeManager = this.coreManager.getManager(TypeManager.class);

            KernelConfiguration kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

            long attribute = LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_EXECUTED,
                    TypeInitializerAttributeType.CAN_BE_INHERITED, TypeInitializerAttributeType.CAN_BE_SHARED_WRITTEN,
                    TypeInitializerAttributeType.HAS_AUDIT, TypeInitializerAttributeType.HAS_CONTENT,
                    TypeInitializerAttributeType.HAS_PERMISSION, TypeInitializerAttributeType.HAS_PROPERTIES);
            Set<UUID> childTypes = Set.of();
            AInfoTypeInitializer typeInitializer = this.coreManager.create(FileSystemFileTypeInitializer.class);

            typeManager.getFactory().buildType(kernelConfiguration.FILES_TYPES_INSTANCE_FILE_ID,
                    kernelConfiguration.FILES_TYPES_INSTANCE_FILE_NAME, attribute, childTypes, typeInitializer);

            attribute = LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_INHERITED,
                    TypeInitializerAttributeType.CAN_BE_SHARED_READ, TypeInitializerAttributeType.HAS_AUDIT,
                    TypeInitializerAttributeType.HAS_CHILD, TypeInitializerAttributeType.HAS_CONTENT,
                    TypeInitializerAttributeType.HAS_PERMISSION, TypeInitializerAttributeType.HAS_PROPERTIES);
            childTypes = Set.of(kernelConfiguration.FILES_TYPES_INSTANCE_FOLDER_ID, kernelConfiguration.FILES_TYPES_INSTANCE_FILE_ID);
            typeInitializer = this.coreManager.create(FileSystemFolderTypeInitializer.class);

            typeManager.getFactory().buildType(kernelConfiguration.FILES_TYPES_INSTANCE_FOLDER_ID,
                    kernelConfiguration.FILES_TYPES_INSTANCE_FOLDER_NAME, attribute, childTypes, typeInitializer);
        }
    }

    @Override
    public void shutdown() {
    }
}
