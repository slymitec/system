package indi.sly.subsystem.periphery.calls.values;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.ADefinition;

public class UserContentResponseDefinition extends ADefinition {
    public UserContentResponseDefinition() {
    }

    private Class<?> clazz;
    private Object value;

    public Class<?> getClazz() {
        return this.clazz;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
        this.clazz = ObjectUtil.isAnyNull(this.value) ? null : this.value.getClass();
    }
}
