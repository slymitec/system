package indi.sly.system.kernel.objects.infotypes.prototypes;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.objects.infotypes.prototypes.wrappers.ATypeInitializer;
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
        if (!this.isTypeInitializerAttributeExist(TypeInitializerAttributeType.HAS_CHILD)) {
            throw new StatusNotSupportedException();
        }

        Set<UUID> result = Collections.unmodifiableSet(this.type.getChildTypes());
        return result;
    }

    public boolean isTypeInitializerAttributeExist(long typeInitializerAttribute) {
        if ((this.type.getAttribute() & typeInitializerAttribute) != 0) {
            return true;
        } else {
            return false;
        }
    }

    public ATypeInitializer getTypeInitializer() {
        return this.type.getTypeInitializer();
    }

    public synchronized int getTotalOccupiedCount() {
        return this.type.getCounter().getTotalOccupiedCount();
    }

    public synchronized void addTotalOccupiedCount() {
        this.type.getCounter().offsetTotalOccupiedCount(1);
    }

    public synchronized void minusTotalOccupiedCount() {
        this.type.getCounter().offsetTotalOccupiedCount(-1);
    }
}
