package indi.sly.system.kernel.objects.infotypes.prototypes;

import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.kernel.core.prototypes.ADefinitionObject;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.infotypes.values.TypeDefinition;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;

import java.util.Set;
import java.util.UUID;

public class TypeObject extends ADefinitionObject<TypeDefinition> {
    public String getName() {
        return this.definition.getName();
    }

    public Set<UUID> getChildTypes() {
        if (!this.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_CHILD)) {
            throw new StatusNotSupportedException();
        }

        Set<UUID> childTypes = this.definition.getChildTypes();

        return CollectionUtil.unmodifiable(childTypes);
    }

    public boolean isTypeInitializerAttributesExist(long typeInitializerAttributes) {
        long attribute = this.definition.getAttribute();

        return LogicalUtil.isAllExist(attribute, typeInitializerAttributes);
    }

    public AInfoTypeInitializer getInitializer() {
        return this.definition.getInitializer();
    }
}
