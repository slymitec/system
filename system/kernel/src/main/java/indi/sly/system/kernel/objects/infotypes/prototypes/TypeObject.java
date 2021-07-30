package indi.sly.system.kernel.objects.infotypes.prototypes;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.objects.infotypes.prototypes.wrappers.ATypeInitializer;
import indi.sly.system.kernel.objects.infotypes.values.TypeCounterDefinition;
import indi.sly.system.kernel.objects.infotypes.values.TypeDefinition;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;

public class TypeObject extends APrototype {
    private TypeDefinition type;

    public void setType(TypeDefinition type) {
        if (ObjectUtil.isAnyNull(this.type)) {
            this.type = type;
        }
    }

    public String getName() {
        return this.type.getName();
    }

    public UUID getThisType() {
        return this.type.getThisType();
    }

    public Set<UUID> getChildTypes() {
        if (!this.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_CHILD)) {
            throw new StatusNotSupportedException();
        }

        return Collections.unmodifiableSet(this.type.getChildTypes());
    }

    public boolean isTypeInitializerAttributesExist(long typeInitializerAttributes) {
        return LogicalUtil.isAllExist(this.type.getAttribute(), typeInitializerAttributes);
    }

    public ATypeInitializer getTypeInitializer() {
        return this.type.getTypeInitializer();
    }

    public TypeCounterObject getCount() {
        TypeCounterObject typeCounter = this.factoryManager.create(TypeCounterObject.class);

        typeCounter.setSource(() -> this.type.getCounter(), (TypeCounterDefinition source) -> {
        });

        return typeCounter;
    }
}
