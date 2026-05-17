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
import indi.sly.system.kernel.objects.prototypes.TypeFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TypeManager extends AManager {
    protected TypeFactory factory;

    public TypeFactory getFactory() {
        return this.factory;
    }

    @Override
    public void startup(long startup) {
        if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_SELF)) {
            this.factory = this.coreManager.create(TypeFactory.class);
            this.factory.init();
        } else if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_KERNEL)) {
            KernelConfigurationDefinition kernelConfiguration = this.coreManager.getKernelSpace().getConfiguration();

            long attribute = LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_SHARED_READ,
                    TypeInitializerAttributeType.CAN_NOT_CHANGE_OWNER, TypeInitializerAttributeType.HAS_AUDIT,
                    TypeInitializerAttributeType.HAS_CHILD, TypeInitializerAttributeType.HAS_CONTENT,
                    TypeInitializerAttributeType.HAS_PERMISSION, TypeInitializerAttributeType.HAS_PROPERTIES);
            Set<UUID> childTypes = Set.of(UUIDUtil.getEmpty());
            AInfoTypeInitializer typeInitializer = this.coreManager.create(FolderTypeInitializer.class);

            this.create(kernelConfiguration.OBJECTS_TYPES_INSTANCE_FOLDER_ID,
                    kernelConfiguration.OBJECTS_TYPES_INSTANCE_FOLDER_NAME, attribute, childTypes, typeInitializer);

            attribute = LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_SHARED_READ,
                    TypeInitializerAttributeType.CAN_NOT_CHANGE_OWNER, TypeInitializerAttributeType.CHILD_IS_NAMELESS,
                    TypeInitializerAttributeType.HAS_AUDIT, TypeInitializerAttributeType.HAS_CHILD,
                    TypeInitializerAttributeType.HAS_CONTENT, TypeInitializerAttributeType.HAS_PERMISSION,
                    TypeInitializerAttributeType.HAS_PROPERTIES
            );
            typeInitializer = this.coreManager.create(NamelessFolderTypeInitializer.class);

            this.create(kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID,
                    kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_NAME, attribute, childTypes, typeInitializer);
        }
    }

    public TypeObject get(UUID typeID) {
        if (ValueUtil.isAnyNullOrEmpty(typeID)) {
            throw new ConditionParametersException();
        }

        return this.coreManager.getObjectCollection().getById(SpaceType.KERNEL, typeID);
    }

    public TypeObject create(UUID typeID, String typeName, long attribute, Set<UUID> childTypes,
                                          AInfoTypeInitializer typeInitializer) {
        if (ObjectUtil.isAnyNull(typeID, childTypes, typeInitializer) || StringUtil.isNameIllegal(typeName)) {
            throw new ConditionParametersException();
        }

        return this.factory.buildType(typeID, typeName, attribute, childTypes, typeInitializer);
    }

    public void delete(UUID typeID) {
        if (ObjectUtil.isAnyNull(typeID)) {
            throw new ConditionParametersException();
        }

        TypeObject type = this.get(typeID);

        this.factory.deleteType(typeID, type);
    }

    public Set<UUID> list() {
        Set<UUID> infoTypeIDs = this.coreManager.getKernelSpace().getInfoTypeIDs();

        return CollectionUtil.unmodifiable(infoTypeIDs);
    }
}
