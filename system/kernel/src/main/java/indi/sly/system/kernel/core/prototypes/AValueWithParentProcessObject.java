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
public abstract class AValueWithParentProcessObject<T1 extends ACoreProcessObject<T1>, T2> extends ACoreProcessObject<T2> {
    protected T1 parent;
    protected T2 value;

    protected void read(T2 source) {
        this.value = source;
    }

    protected T2 write() {
        return this.value;
    }

    public final void setParentAndSource(T1 parent, Provider<T2> funcRead,
                                         Consumer1<T2> funcWrite) {
        if (ObjectUtil.isAnyNull(parent, funcRead, funcWrite)) {
            throw new ConditionParametersException();
        }

        this.parent = parent;

        this.setParent(this.parent);
        this.setSource(funcRead, funcWrite);
    }
}
