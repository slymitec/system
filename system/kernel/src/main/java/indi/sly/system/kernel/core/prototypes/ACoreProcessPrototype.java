package indi.sly.system.kernel.core.prototypes;


import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ObjectUtil;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ACoreProcessPrototype<T> extends APrototype {
    private Consumer funcParentInit;
    private Consumer funcParentFresh;
    private Provider<T> funcRead;
    private Consumer1<T> funcWrite;
    private Consumer1<Long> funcLock;

    public final void setParent(ACoreProcessPrototype<?> parentCoreProcess) {
        if (ObjectUtil.allNotNull(parentCoreProcess)) {
            this.funcParentInit = parentCoreProcess::init;
            this.funcParentFresh = parentCoreProcess::fresh;
        } else {
            this.funcParentInit = null;
            this.funcParentFresh = null;
        }
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
        if (ObjectUtil.isAnyNull(this.funcRead, this.funcWrite)) {
            throw new StatusRelationshipErrorException();
        }

        if (ObjectUtil.allNotNull(this.funcLock)) {
            this.funcLock.accept(lockType);
        }
    }

    protected final void init() {
        if (ObjectUtil.isAnyNull(this.funcRead, this.funcWrite)) {
            throw new StatusRelationshipErrorException();
        }

        T value = this.funcRead.acquire();
        this.read(value);

        if (ObjectUtil.allNotNull(this.funcParentInit)) {
            this.funcParentInit.accept();
        }
    }

    protected final void fresh() {
        if (ObjectUtil.isAnyNull(this.funcRead, this.funcWrite)) {
            throw new StatusRelationshipErrorException();
        }

        T value = this.write();
        this.funcWrite.accept(value);

        if (ObjectUtil.allNotNull(this.funcParentFresh)) {
            this.funcParentFresh.accept();
        }
    }

    protected abstract void read(T source);

    protected abstract T write();
}
