package indi.sly.system.kernel.core.prototypes;


import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.MethodScopeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

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

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public final void setParent(T2 parent) {
        this.parent = parent;

        if (ObjectUtil.allNotNull(this.parent)) {
            if (this.parent instanceof AProcessObject parentProcess) {
                this.funcParentInit = parentProcess::init;
                this.funcParentFresh = parentProcess::fresh;
                this.funcParentLock = parentProcess::lock;
            }
        } else {
            this.funcParentInit = null;
            this.funcParentFresh = null;
            this.funcParentLock = null;
        }
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public final void setSource(Provider<T1> funcRead, Consumer1<T1> funcWrite) {
        if (ObjectUtil.isAnyNull(funcRead, funcWrite)) {
            throw new ConditionParametersException();
        }

        this.funcRead = funcRead;
        this.funcWrite = funcWrite;
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public final void setLock(Consumer1<Long> funcLock) {
        if (ObjectUtil.isAnyNull(funcLock)) {
            throw new ConditionParametersException();
        }

        this.funcLock = funcLock;
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
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

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    protected final void init() {
        if (ObjectUtil.isAnyNull(this.funcRead, this.funcWrite)) {
            throw new ConditionContextException();
        }

        if (ObjectUtil.allNotNull(this.funcParentInit)) {
            this.funcParentInit.accept();
        }

        T1 value = this.funcRead.acquire();
        this.read(value);
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
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

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    protected abstract void read(T1 source);

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    protected abstract T1 write();
}
