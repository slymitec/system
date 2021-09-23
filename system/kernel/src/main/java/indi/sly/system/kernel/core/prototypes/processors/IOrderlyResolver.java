package indi.sly.system.kernel.core.prototypes.processors;

public interface IOrderlyResolver extends Comparable<IOrderlyResolver> {
    int order();
}
