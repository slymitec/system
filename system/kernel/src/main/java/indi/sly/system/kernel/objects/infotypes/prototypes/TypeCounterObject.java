package indi.sly.system.kernel.objects.infotypes.prototypes;

import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AValueProcessObject;
import indi.sly.system.kernel.objects.infotypes.values.TypeCounterDefinition;

public class TypeCounterObject extends AValueProcessObject<TypeCounterDefinition> {
    public synchronized int getTotalOccupiedCount() {
        this.init();

        return this.value.getTotalOccupiedCount();
    }

    public synchronized void addTotalOccupiedCount() {
        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetTotalOccupiedCount(1);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public synchronized void minusTotalOccupiedCount() {
        this.lock(LockType.WRITE);
        this.init();

        this.value.offsetTotalOccupiedCount(-1);

        this.fresh();
        this.lock(LockType.NONE);
    }
}
