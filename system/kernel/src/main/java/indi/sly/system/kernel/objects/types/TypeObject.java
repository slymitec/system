package indi.sly.system.kernel.objects.types;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.ACoreObject;

public class TypeObject extends ACoreObject {
    private TypeDefinition type;

    public void setType(TypeDefinition type) {
        if (ObjectUtils.isAnyNull(this.type)) {
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

    public int getTotalOccupiedCount() {
        return this.type.getCounter().getTotalOccupiedCount();
    }

    public void addTotalOccupiedCount() {
        synchronized (this.type) {
            this.type.getCounter().setTotalOccupiedCount(this.type.getCounter().getTotalOccupiedCount() + 1);
        }
    }

    public void minusTotalOccupiedCount() {
        synchronized (this.type) {
            this.type.getCounter().setTotalOccupiedCount(this.type.getCounter().getTotalOccupiedCount() - 1);
        }
    }
}
