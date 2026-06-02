package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.core.prototypes.ObjectCollectionObject;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.infotypes.values.TypeDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TypeFactory extends AFactory {
    @Override
    public void init() {
    }

    private TypeObject createType(UUID typeID, TypeDefinition definition) {
        TypeObject type = this.coreManager.create(TypeObject.class);

        type.setDefinition(definition);

        ObjectCollectionObject objectCollection = this.coreManager.getObjectCollection();
        objectCollection.addById(SpaceType.KERNEL, typeID, type);

        return type;
    }

    public TypeObject buildType(UUID typeID, String typeName, long attribute, Set<UUID> childTypes,
                                AInfoTypeInitializer typeInitializer) {
        if (ObjectUtil.isAnyNull(typeID, childTypes, typeInitializer) || StringUtil.isNameIllegal(typeName)) {
            throw new ConditionParametersException();
        }

        TypeDefinition type = new TypeDefinition();
        type.setName(typeName);
        type.setAttribute(attribute);
        type.setInitializer(typeInitializer);
        if (ObjectUtil.allNotNull(childTypes)) {
            type.getChildTypes().addAll(childTypes);
        }

        Set<UUID> infoTypeIDs = this.coreManager.getKernelSpace().getInfoTypeIds();

        if (infoTypeIDs.contains(typeID)) {
            throw new StatusAlreadyExistedException();
        }

        infoTypeIDs.add(typeID);

        typeInitializer.install();

        return this.createType(typeID, type);
    }

    public void deleteType(UUID typeID, TypeObject type) {
        if (ObjectUtil.isAnyNull(typeID)) {
            throw new ConditionParametersException();
        }

        Set<UUID> infoTypeIDs = this.coreManager.getKernelSpace().getInfoTypeIds();

        type.getInitializer().uninstall();

        ObjectCollectionObject objectCollection = this.coreManager.getObjectCollection();
        objectCollection.deleteById(SpaceType.KERNEL, typeID);

        infoTypeIDs.remove(typeID);
    }
}
