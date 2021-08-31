package indi.sly.system.kernel.objects.infotypes.prototypes;

import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AIndependentValueProcessObject;
import indi.sly.system.kernel.objects.infotypes.prototypes.processors.AInfoTypeInitializer;
import indi.sly.system.kernel.objects.infotypes.values.TypeCounterDefinition;
import indi.sly.system.kernel.objects.infotypes.values.TypeDefinition;
import indi.sly.system.kernel.objects.infotypes.values.TypeInitializerAttributeType;

import java.util.Set;
import java.util.UUID;

public class TypeObject extends AIndependentValueProcessObject<TypeDefinition> {
    public String getName() {
        this.lock(LockType.READ);
        this.init();

        String name = this.value.getName();

        this.lock(LockType.NONE);
        return name;
    }

    public UUID getThisType() {
        this.lock(LockType.READ);
        this.init();

        UUID thisType = this.value.getThisType();

        this.lock(LockType.NONE);
        return thisType;
    }

    public Set<UUID> getChildTypes() {
        if (!this.isTypeInitializerAttributesExist(TypeInitializerAttributeType.HAS_CHILD)) {
            throw new StatusNotSupportedException();
        }

        this.lock(LockType.READ);
        this.init();

        Set<UUID> childTypes = this.value.getChildTypes();

        this.lock(LockType.NONE);
        return CollectionUtil.unmodifiable(childTypes);
    }

    public boolean isTypeInitializerAttributesExist(long typeInitializerAttributes) {
        this.lock(LockType.READ);
        this.init();

        long attribute = this.value.getAttribute();

        this.lock(LockType.NONE);
        return LogicalUtil.isAllExist(attribute, typeInitializerAttributes);
    }

    public AInfoTypeInitializer getInitializer() {
        this.lock(LockType.READ);
        this.init();

        AInfoTypeInitializer initializer = this.value.getInitializer();

        this.lock(LockType.NONE);
        return initializer;
    }

    public TypeCounterObject getCount() {
        TypeCounterObject typeCounter = this.factoryManager.create(TypeCounterObject.class);

        typeCounter.setParent(this);
        typeCounter.setSource(() -> this.value.getCounter(), (TypeCounterDefinition source) -> {
        });

        return typeCounter;
    }
}
