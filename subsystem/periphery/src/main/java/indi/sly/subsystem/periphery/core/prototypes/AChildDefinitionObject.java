package indi.sly.subsystem.periphery.core.prototypes;

import indi.sly.system.common.values.ADefinition;

public class AChildDefinitionObject<T extends ADefinition, P extends ADefinitionObject<?>> extends ADefinitionObject<T> {
    @SuppressWarnings("NullAway.Init")
    protected P base;

    public void setBase(P base) {
        this.base = base;
    }
}
