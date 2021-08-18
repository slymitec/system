package indi.sly.system.kernel.core.prototypes;


import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ObjectUtil;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AProcessObject<T1, T2> extends AObject {
    protected T2 parent;

    private Consumer funcParentInit;
    private Consumer funcParentFresh;
    private Consumer1<Long> funcParentLock;
    private Provider<T1> funcRead;
    private Consumer1<T1> funcWrite;
    private Consumer1<Long> funcLock;

    public final void setParent(T2 parent) {
        if (ObjectUtil.allNotNull(parent)) {
            this.parent = parent;

            if (parent instanceof AProcessObject) {
                this.funcParentInit = ((AProcessObject<?, ?>) parent)::init;
                this.funcParentFresh = ((AProcessObject<?, ?>) parent)::fresh;
                this.funcParentLock = ((AProcessObject<?, ?>) parent)::lock;

                return;
            }
        }

        this.funcParentInit = null;
        this.funcParentFresh = null;
        this.funcParentLock = null;
    }

    public final void setSource(Provider<T1> funcRead, Consumer1<T1> funcWrite) {
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

    protected final void lock(long lock) {
        if (ObjectUtil.isAnyNull(this.funcRead, this.funcWrite)) {
            throw new ConditionContextException();
        }

        if (ObjectUtil.allNotNull(this.funcLock)) {
            this.funcLock.accept(lock);
        }
        if (ObjectUtil.allNotNull(this.funcParentLock)) {
            this.funcParentLock.accept(lock);
        }
    }

    protected final void init() {
        if (ObjectUtil.isAnyNull(this.funcRead, this.funcWrite)) {
            throw new ConditionContextException();
        }

        T1 value = this.funcRead.acquire();
        this.read(value);

        if (ObjectUtil.allNotNull(this.funcParentInit)) {
            this.funcParentInit.accept();
        }
    }

    protected final void fresh() {
        if (ObjectUtil.isAnyNull(this.funcRead, this.funcWrite)) {
            throw new ConditionContextException();
        }

        T1 value = this.write();
        this.funcWrite.accept(value);

        if (ObjectUtil.allNotNull(this.funcParentFresh)) {
            this.funcParentFresh.accept();
        }
    }

    protected abstract void read(T1 source);

    protected abstract T1 write();
}
