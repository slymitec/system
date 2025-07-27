package indi.sly.system.services.jobs.values;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.ADefinition;

public class UserContentResponseResultDefinition extends ADefinition<UserContentResponseResultDefinition> {
    public UserContentResponseResultDefinition() {
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
