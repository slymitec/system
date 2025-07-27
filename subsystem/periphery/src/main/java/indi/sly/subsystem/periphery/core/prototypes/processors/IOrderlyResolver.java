package indi.sly.subsystem.periphery.core.prototypes.processors;

public interface IOrderlyResolver extends Comparable<IOrderlyResolver> {
    int order();
}
