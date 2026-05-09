package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.values.ADefinition;

public class AChildDefinitionObject<T extends ADefinition, P extends ADefinitionObject<?>> extends ADefinitionObject<T> {
    protected P base;

    public void setBase(P base) {
        this.base = base;
    }
}
