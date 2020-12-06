package indi.sly.system.kernel.core.prototypes;


import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.StatusNotSupportedException;
import indi.sly.system.common.functions.Consumer;
import indi.sly.system.common.functions.Consumer0;
import indi.sly.system.common.functions.Provider;
import indi.sly.system.common.utility.ObjectUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ACoreProcessPrototype<T> extends ACorePrototype {
    private Consumer0 funcInit;
    private Consumer0 funcFresh;
    private Provider<T> funcRead;
    private Consumer<T> funcWrite;
    private Consumer<Long> funcLock;

    public final void setParent(ACoreProcessPrototype parentCoreProcess) {
        if (ObjectUtils.isAnyNull(parentCoreProcess)) {
            throw new ConditionParametersException();
        }

        this.funcInit = () -> {
            parentCoreProcess.init();
        };
        this.funcFresh = () -> {
            parentCoreProcess.fresh();
        };
    }

    public final void setSource(Provider<T> funcRead, Consumer<T> funcWrite) {
        if (ObjectUtils.isAnyNull(funcRead, funcWrite)) {
            throw new ConditionParametersException();
        }

        this.funcRead = funcRead;
        this.funcWrite = funcWrite;
    }

    public final void setLock(Consumer<Long> funcLock) {
        if (ObjectUtils.isAnyNull(funcLock)) {
            throw new ConditionParametersException();
        }

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

        if (ObjectUtils.allNotNull(this.funcInit)) {
            this.funcInit.accept();
        }
    }

    protected final void fresh() {
        T value = this.write();
        this.funcWrite.accept(value);

        if (ObjectUtils.allNotNull(this.funcFresh)) {
            this.funcFresh.accept();
        }
    }

    protected abstract void read(T source);

    protected abstract T write();
}
