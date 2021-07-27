package indi.sly.system.kernel.core.prototypes.processors;

public interface IOrderlyResolver extends IResolver, Comparable<IOrderlyResolver> {
    int order();

    @Override
    default int compareTo(IOrderlyResolver other) {
        return this.order() - other.order();
    }
}
