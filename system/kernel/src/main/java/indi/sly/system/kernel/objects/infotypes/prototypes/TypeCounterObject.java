package indi.sly.system.kernel.objects.infotypes.prototypes;

import indi.sly.system.kernel.core.prototypes.AValueProcessObject;
import indi.sly.system.kernel.objects.infotypes.values.TypeCounterDefinition;

public class TypeCounterObject extends AValueProcessObject<TypeCounterDefinition> {
    public synchronized int getTotalOccupiedCount() {
        return this.value.getTotalOccupiedCount();
    }

    public synchronized void addTotalOccupiedCount() {
        this.value.offsetTotalOccupiedCount(1);
    }

    public synchronized void minusTotalOccupiedCount() {
        this.value.offsetTotalOccupiedCount(-1);
    }
}
