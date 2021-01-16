package indi.sly.system.kernel.core.prototypes;


import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.common.lang.Consumer;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.ObjectUtil;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ACoreProcessPrototype<T> extends APrototype {
    private Consumer funcInit;
    private Consumer funcFresh;
    private Provider<T> funcRead;
    private Consumer1<T> funcWrite;
    private Consumer1<Long> funcLock;

    public final void setParent(ACoreProcessPrototype parentCoreProcess) {
        if (ObjectUtil.isAnyNull(parentCoreProcess)) {
            throw new ConditionParametersException();
        }

        this.funcInit = () -> {
            parentCoreProcess.init();
        };
        this.funcFresh = () -> {
            parentCoreProcess.fresh();
        };
    }

    public final void setSource(Provider<T> funcRead, Consumer1<T> funcWrite) {
        if (ObjectUtil.isAnyNull(funcRead, funcWrite)) {
            throw new ConditionParametersException();
        }

        this.funcRead = funcRead;
        this.funcWrite = funcWrite;
    }

    public final void setLock(Consumer1<Long> funcLock) {
        if (ObjectUtil.isAnyNull(funcLock)) {
            throw new ConditionParametersException();
        }

        this.funcLock = funcLock;
    }

    protected final void lock(long lockType) {
        if (ObjectUtil.allNotNull(this.funcLock)) {
            this.funcLock.accept(lockType);
        }
    }

    protected final void init() {
        T value = this.funcRead.acquire();
        this.read(value);

        if (ObjectUtil.allNotNull(this.funcInit)) {
            this.funcInit.accept();
        }
    }

    protected final void fresh() {
        T value = this.write();
        this.funcWrite.accept(value);

        if (ObjectUtil.allNotNull(this.funcFresh)) {
            this.funcFresh.accept();
        }
    }

    protected abstract void read(T source);

    protected abstract T write();
}
