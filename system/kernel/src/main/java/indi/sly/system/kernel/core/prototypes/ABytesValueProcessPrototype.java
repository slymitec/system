package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.supports.ObjectUtil;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ABytesValueProcessPrototype<T> extends ACoreProcessPrototype<byte[]> {
    protected T value;

    protected void read(byte[] source) {
        if (ObjectUtil.isAnyNull(source)) {
            this.value = null;
        } else {
            this.value = ObjectUtil.transferFromByteArray(source);
        }
    }

    protected byte[] write() {
        return ObjectUtil.isAnyNull(this.value) ? null : ObjectUtil.transferToByteArray(this.value);
    }
}
