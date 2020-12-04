package indi.sly.system.kernel.core.prototypes;


import indi.sly.system.common.exceptions.StatusNotSupportedException;
import indi.sly.system.common.functions.Consumer;
import indi.sly.system.common.functions.Provider;
import indi.sly.system.common.utility.ObjectUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ACoreProcessPrototype<T> extends ACorePrototype {
    private Provider<T> funcRead;
    private Consumer<T> funcWrite;
    private Consumer<Long> funcLock;

    public final void setSource(Provider<T> funcRead, Consumer<T> funcWrite) {
        this.funcRead = funcRead;
        this.funcWrite = funcWrite;
    }

    public final void setLock(Consumer<Long> funcLock) {
        this.funcLock = funcLock;
    }

    protected final void lock(long lockType) {
        if (ObjectUtils.isAnyNull(this.funcLock)) {
            throw new StatusNotSupportedException();
        }

        this.funcLock.accept(lockType);
    }

    protected final void init() {
        T value = this.funcRead.acquire();
        this.read(value);
    }

    protected final void fresh() {
        T value = this.write();
        this.funcWrite.accept(value);
    }

    protected abstract void read(T source);

    protected abstract T write();
}
