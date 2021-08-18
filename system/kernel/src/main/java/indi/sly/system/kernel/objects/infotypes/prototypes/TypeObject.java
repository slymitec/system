package indi.sly.system.kernel.objects.infotypes.prototypes;

import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.kernel.core.prototypes.AValueProcessObject;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.infotypes.values.TypeCounterDefinition;
import indi.sly.system.kernel.objects.infotypes.values.TypeDefinition;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;

import java.util.Set;
import java.util.UUID;

public class TypeObject extends AValueProcessObject<TypeDefinition> {
    public String getName() {
        this.init();

        return this.value.getName();
    }

    public UUID getThisType() {
        this.init();

        return this.value.getThisType();
    }

    public Set<UUID> getChildTypes() {
        if (!this.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_CHILD)) {
            throw new StatusNotSupportedException();
        }

        this.init();

        return CollectionUtil.unmodifiable(this.value.getChildTypes());
    }

    public boolean isTypeInitializerAttributesExist(long typeInitializerAttributes) {
        this.init();

        return LogicalUtil.isAllExist(this.value.getAttribute(), typeInitializerAttributes);
    }

    public AInfoTypeInitializer getInitializer() {
        this.init();

        return this.value.getInitializer();
    }

    public TypeCounterObject getCount() {
        TypeCounterObject typeCounter = this.factoryManager.create(TypeCounterObject.class);

        typeCounter.setParent(this);
        typeCounter.setSource(() -> this.value.getCounter(), (TypeCounterDefinition source) -> {
        });

        return typeCounter;
    }
}
