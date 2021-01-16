package indi.sly.system.kernel.objects;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Named;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.*;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.objects.infotypes.prototypes.wrappers.ATypeInitializer;
import indi.sly.system.kernel.objects.infotypes.values.TypeDefinition;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.instances.prototypes.wrappers.FolderTypeInitializer;
import indi.sly.system.kernel.objects.instances.prototypes.wrappers.NamelessFolderTypeInitializer;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TypeManager extends AManager {
    @Override
    public void startup(long startupTypes) {
        if (startupTypes == StartupType.STEP_INIT) {
        } else if (startupTypes == StartupType.STEP_KERNEL) {
            KernelConfigurationDefinition kernelConfiguration = this.factoryManager.getKernelSpace().getConfiguration();

            Set<UUID> childTypes = new HashSet<>();
            childTypes.add(UUIDUtil.getEmpty());

            this.create(kernelConfiguration.OBJECTS_TYPES_INSTANCE_FOLDER_ID,
                    kernelConfiguration.OBJECTS_TYPES_INSTANCE_FOLDER_NAME,
                    LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_SENT_AND_INHERITED,
                            TypeInitializerAttributeType.CAN_BE_SHARED_READ, TypeInitializerAttributeType.HAS_AUDIT,
                            TypeInitializerAttributeType.HAS_CHILD, TypeInitializerAttributeType.HAS_CONTENT,
                            TypeInitializerAttributeType.HAS_PERMISSION,
                            TypeInitializerAttributeType.HAS_PROPERTIES),
                    childTypes, this.factoryManager.create(FolderTypeInitializer.class));

            this.create(kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_ID,
                    kernelConfiguration.OBJECTS_TYPES_INSTANCE_NAMELESSFOLDER_NAME,
                    LogicalUtil.or(TypeInitializerAttributeType.CAN_BE_SENT_AND_INHERITED,
                            TypeInitializerAttributeType.CAN_BE_SHARED_READ,
                            TypeInitializerAttributeType.CHILD_IS_NAMELESS,
                            TypeInitializerAttributeType.HAS_AUDIT, TypeInitializerAttributeType.HAS_CHILD,
                            TypeInitializerAttributeType.HAS_CONTENT,
                            TypeInitializerAttributeType.HAS_PERMISSION, TypeInitializerAttributeType.HAS_PROPERTIES),
                    childTypes, this.factoryManager.create(NamelessFolderTypeInitializer.class));
        }
    }

    public TypeObject get(UUID typeID) {
        if (ValueUtil.isAnyNullOrEmpty(typeID)) {
            throw new ConditionParametersException();
        }

        TypeObject type = this.factoryManager.getCoreRepository().getByID(SpaceType.KERNEL, TypeObject.class,
                typeID);

        return type;
    }

    public TypeObject getIDByName(String typeName) {
        if (StringUtil.isNameIllegal(typeName)) {
            throw new ConditionParametersException();
        }

        TypeObject type = this.factoryManager.getCoreRepository().getByName(SpaceType.KERNEL, TypeObject.class
                , "Objects_Types_" + typeName);

        return type;
    }

    public synchronized TypeObject create(UUID typeID, String typeName, long attribute, Set<UUID> childTypes,
                                          ATypeInitializer typeInitializer) {
        if (ObjectUtil.isAnyNull(typeID, childTypes, typeInitializer) || StringUtil.isNameIllegal(typeName)) {
            throw new ConditionParametersException();
        }

        TypeDefinition typeDefinition = new TypeDefinition();
        typeDefinition.setId(UUIDUtil.createRandom());
        typeDefinition.setName(typeName);
        typeDefinition.setAttribute(attribute);
        typeDefinition.setTypeInitializer(typeInitializer);
        if (ObjectUtil.allNotNull(childTypes)) {
            typeDefinition.getChildTypes().addAll(childTypes);
        }

        TypeObject typeObject = this.factoryManager.create(TypeObject.class);
        typeObject.setType(typeDefinition);

        Set<UUID> objectTypes = this.factoryManager.getKernelSpace().getPrototypeTypes();

        if (objectTypes.contains(typeID)) {
            throw new StatusAlreadyExistedException();
        }

        this.factoryManager.getCoreRepository().add(SpaceType.KERNEL, typeID, "Objects_Types_" + typeName,
                typeObject);
        objectTypes.add(typeID);

        typeInitializer.install();

        return typeObject;
    }

    public synchronized void delete(UUID typeID) {
        if (ObjectUtil.isAnyNull(typeID)) {
            throw new ConditionParametersException();
        }

        Set<UUID> objectTypes = this.factoryManager.getKernelSpace().getPrototypeTypes();

        TypeObject type = this.get(typeID);

        type.getTypeInitializer().uninstall();

        this.factoryManager.getCoreRepository().deleteByID(SpaceType.KERNEL, TypeObject.class, typeID);
        objectTypes.remove(typeID);
    }

    public Set<UUID> list() {
        Set<UUID> objectTypes = this.factoryManager.getKernelSpace().getPrototypeTypes();

        return Collections.unmodifiableSet(objectTypes);
    }
}
