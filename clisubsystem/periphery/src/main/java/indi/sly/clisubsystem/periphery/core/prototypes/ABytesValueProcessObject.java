package indi.sly.clisubsystem.periphery.core.prototypes;

import indi.sly.system.common.supports.ObjectUtil;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ABytesValueProcessObject<T1, T2> extends AProcessObject<byte[], T2> {
    protected T1 value;

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
