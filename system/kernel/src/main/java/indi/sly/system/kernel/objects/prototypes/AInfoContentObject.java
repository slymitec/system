package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.lang.ConditionContextException;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Consumer;
import indi.sly.system.common.lang.StatusDisabilityException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AValueProcessObject;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AInfoContentObject extends AValueProcessObject<byte[], InfoObject> {
    private Consumer funcExecute;

    protected InfoOpenDefinition infoOpen;

    public final void setExecute(Consumer funcExecute) {
        if (ObjectUtil.isAnyNull(funcExecute)) {
            throw new ConditionParametersException();
        }

        this.funcExecute = funcExecute;
    }

    public void setInfoOpen(InfoOpenDefinition infoOpen) {
        this.infoOpen = infoOpen;
    }

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
