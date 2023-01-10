package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.ABuilder;
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
public class TypeBuilder extends ABuilder {
    protected TypeFactory factory;

    public TypeObject create(UUID typeID, String typeName, long attribute, Set<UUID> childTypes,
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

        Set<UUID> infoTypeIDs = this.factoryManager.getKernelSpace().getInfoTypeIDs();

        if (infoTypeIDs.contains(typeID)) {
            throw new StatusAlreadyExistedException();
        }

        infoTypeIDs.add(typeID);

        typeInitializer.install();

        return this.factory.buildType(typeID, type);
    }

    public synchronized void delete(UUID typeID, TypeObject type) {
        if (ObjectUtil.isAnyNull(typeID)) {
            throw new ConditionParametersException();
        }

        Set<UUID> infoTypeIDs = this.factoryManager.getKernelSpace().getInfoTypeIDs();

        type.getInitializer().uninstall();
        type.uncache(SpaceType.KERNEL);

        infoTypeIDs.remove(typeID);
    }
}
