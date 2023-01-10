package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.objects.infotypes.prototypes.TypeObject;
import indi.sly.system.kernel.objects.infotypes.values.TypeDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TypeFactory extends AFactory {
    private TypeObject buildType(UUID typeID, Provider<TypeDefinition> funcRead, Consumer1<TypeDefinition> funcWrite) {
        TypeObject type = this.factoryManager.create(TypeObject.class);

        type.setSource(funcRead, funcWrite);

        type.cache(SpaceType.KERNEL, typeID);

        return type;
    }

    public TypeObject buildType(UUID typeID, TypeDefinition type) {
        if (ValueUtil.isAnyNullOrEmpty(type) || ObjectUtil.isAnyNull(type)) {
            throw new ConditionParametersException();
        }

        return this.buildType(typeID, () -> type, (source) -> {
        });
    }

    public TypeBuilder createType() {
        TypeBuilder typeBuilder = this.factoryManager.create(TypeBuilder.class);

        typeBuilder.factory = this;

        return typeBuilder;
    }
}
