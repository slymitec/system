package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.ObjectUtil;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ABytesWithParentValueProcessObject<T1 extends ACoreProcessObject<T1>, T2> extends ACoreProcessObject<byte[]> {
    protected T1 parent;
    protected T2 value;

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

    public final void setParentAndSource(T1 parent, Provider<byte[]> funcRead,
                                         Consumer1<byte[]> funcWrite) {
        if (ObjectUtil.isAnyNull(parent, funcRead, funcWrite)) {
            throw new ConditionParametersException();
        }

        this.parent = parent;

        this.setParent(this.parent);
        this.setSource(funcRead, funcWrite);
    }
}
