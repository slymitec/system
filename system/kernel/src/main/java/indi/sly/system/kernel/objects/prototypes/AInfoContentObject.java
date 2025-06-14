package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.values.MethodScopeType;
import indi.sly.system.kernel.core.prototypes.AValueProcessObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AInfoContentObject extends AValueProcessObject<byte[], InfoObject> {
    private Consumer funcExecute;
    protected Consumer funcCustomRead;
    protected Consumer funcCustomWrite;

    protected InfoOpenDefinition infoOpen;

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public final void setExecute(Consumer funcExecute) {
        if (ObjectUtil.isAnyNull(funcExecute)) {
            throw new ConditionParametersException();
        }

        this.funcExecute = funcExecute;
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public void setInfoOpen(InfoOpenDefinition infoOpen) {
        this.infoOpen = infoOpen;
    }

    protected final void read(byte[] source) {
        super.read(source);

        if (ObjectUtil.allNotNull(this.funcCustomRead)) {
            this.funcCustomRead.accept();
        }
    }

    protected final byte[] write() {
        if (LogicalUtil.isAnyEqual(infoOpen.getAttribute(), InfoOpenAttributeType.OPEN_ONLY_READ)) {
            throw new ConditionRefuseException();
        }

        if (ObjectUtil.allNotNull(this.funcCustomRead)) {
            this.funcCustomWrite.accept();
        }

        return super.write();
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public synchronized void close() {
        this.parent.close();

        this.parent = null;
        this.infoOpen = null;
        this.setParent(null);
        this.setSource(() -> {
            throw new ConditionContextException();
        }, (value) -> {
            throw new ConditionContextException();
        });
        this.setLock((lock) -> {
            throw new ConditionContextException();
        });
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public synchronized void execute() {
        if (ObjectUtil.isAnyNull(this.funcExecute)) {
            throw new StatusDisabilityException();
        }

        try {
            this.lock(LockType.READ);
            this.init();

            this.funcExecute.accept();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
