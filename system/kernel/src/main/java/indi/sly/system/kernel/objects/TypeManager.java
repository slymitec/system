package indi.sly.system.kernel.objects;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.*;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.objects.instances.prototypes.processors.FolderTypeInitializer;
import indi.sly.system.kernel.objects.instances.prototypes.processors.NamelessFolderTypeInitializer;
import indi.sly.system.kernel.objects.prototypes.TypeBuilder;
import indi.sly.system.kernel.objects.prototypes.TypeFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TypeManager extends AManager {
    protected TypeFactory factory;

    @Override
    public void startup(long startup) {
        if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_SELF)) {
            this.factory = this.factoryManager.create(TypeFactory.class);
            this.factory.init();
        } else if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_KERNEL)) {
            KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

            Set<UUID> childTypes = Set.of(UUIDUtil.getEmpty());
            long attribute = LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_SENT_AND_INHERITED,
                    TypeInitializerAttributeType.CAN_BE_SHARED_READ, TypeInitializerAttributeType.HAS_AUDIT,
                    TypeInitializerAttributeType.HAS_CHILD, TypeInitializerAttributeType.HAS_CONTENT,
                    TypeInitializerAttributeType.HAS_PERMISSION, TypeInitializerAttributeType.HAS_PROPERTIES);

            this.create(kernelConfiguration.OBJECTS_TYPES_INSTANCE_FOLDER_ID,
                    kernelConfiguration.OBJECTS_TYPES_INSTANCE_FOLDER_NAME, attribute, childTypes,
                    this.factoryManager.create(FolderTypeInitializer.class));

            attribute = LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_SENT_AND_INHERITED,
                    TypeInitializerAttributeType.CAN_BE_SHARED_READ, TypeInitializerAttributeType.CHILD_IS_NAMELESS,
                    TypeInitializerAttributeType.HAS_AUDIT, TypeInitializerAttributeType.HAS_CHILD,
                    TypeInitializerAttributeType.HAS_CONTENT, TypeInitializerAttributeType.HAS_PERMISSION,
                    TypeInitializerAttributeType.HAS_PROPERTIES);

            this.create(kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID,
                    kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_NAME, attribute, childTypes,
                    this.factoryManager.create(NamelessFolderTypeInitializer.class));
        }
    }

    public TypeObject get(UUID typeID) {
        if (ValueUtil.isAnyNullOrEmpty(typeID)) {
            throw new ConditionParametersException();
        }

        TypeObject type = this.factoryManager.getCoreObjectRepository().getByHandle(SpaceType.KERNEL, typeID);

        return type;
    }

    public synchronized TypeObject create(UUID typeID, String typeName, long attribute, Set<UUID> childTypes,
                                          AInfoTypeInitializer initializer) {
        if (ObjectUtil.isAnyNull(typeID, childTypes, initializer) || StringUtil.isNameIllegal(typeName)) {
            throw new ConditionParametersException();
        }

        TypeBuilder typeBuilder = this.factory.createType();

        return typeBuilder.create(typeID, typeName, attribute, childTypes, initializer);
    }

    public synchronized void delete(UUID typeID) {
        if (ObjectUtil.isAnyNull(typeID)) {
            throw new ConditionParametersException();
        }

        TypeObject type = this.get(typeID);

        TypeBuilder typeBuilder = this.factory.createType();

        typeBuilder.delete(typeID, type);
    }

    public Set<UUID> list() {
        Set<UUID> infoTypeIDs = this.factoryManager.getKernelSpace().getInfoTypeIDs();

        return CollectionUtil.unmodifiable(infoTypeIDs);
    }
}
