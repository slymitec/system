package indi.sly.system.kernel.objects.infotypes.values;

import java.util.concurrent.atomic.AtomicInteger;

public class TypeCounterDefinition {
    public TypeCounterDefinition() {
        this.totalOccupiedCount = new AtomicInteger();
    }

    private AtomicInteger totalOccupiedCount;

    public int getTotalOccupiedCount() {
        return this.totalOccupiedCount.get();
    }

    public void offsetTotalOccupiedCount(int offset) {
        this.totalOccupiedCount.getAndAdd(offset);
    }

    public void setTotalOccupiedCount(int totalOccupiedCount) {
        this.totalOccupiedCount.set(totalOccupiedCount);
    }
}
