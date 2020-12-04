package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.utility.ObjectUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ABytesValueProcessPrototype<T> extends ACoreProcessPrototype<byte[]> {
    protected T value;

    protected void read(byte[] source) {
        if (ObjectUtils.isAnyNull(source)) {
            this.value = null;
        } else {
            this.value = ObjectUtils.transferFromByteArray(source);
        }
    }

    protected byte[] write() {
        return ObjectUtils.isAnyNull(this.value) ? null : ObjectUtils.transferToByteArray(this.value);
    }
}
