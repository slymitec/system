package indi.sly.system.kernel.core.prototypes;


import indi.sly.system.common.exceptions.StatusNotSupportedException;
import indi.sly.system.common.functions.Consumer;
import indi.sly.system.common.utility.ObjectUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ACoreProcessPrototype extends ACorePrototype {
    private Consumer<Long> funcLock;

    public final void setLock(Consumer<Long> funcLock) {
        this.funcLock = funcLock;
    }

    protected final void lock(long lockType) {
        if (ObjectUtils.isAnyNull(this.funcLock)) {
            throw new StatusNotSupportedException();
        }

        this.funcLock.accept(lockType);
    }

    protected abstract void init();

    protected abstract void fresh();
}
