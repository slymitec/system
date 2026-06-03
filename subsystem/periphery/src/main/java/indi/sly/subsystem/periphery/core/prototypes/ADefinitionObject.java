package indi.sly.subsystem.periphery.core.prototypes;

import indi.sly.system.common.values.ADefinition;

public class ADefinitionObject<T extends ADefinition> extends AObject {
    protected T definition;

    public T getDefinition() {
        return definition;
    }

    public void setDefinition(T definition) {
        this.definition = definition;
    }
}
