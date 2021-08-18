package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.AIndependentProcessObject;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AInfoContentObject extends AIndependentProcessObject<byte[]> {
    private Consumer funcExecute;

    protected InfoObject info;
    protected InfoOpenDefinition infoOpen;

    public final void setExecute(Consumer funcExecute) {
        if (ObjectUtil.isAnyNull(funcExecute)) {
            throw new ConditionParametersException();
        }

        this.funcExecute = funcExecute;
    }

    public void setInfo(InfoObject info) {
        this.info = info;
    }

    public void setInfoOpen(InfoOpenDefinition infoOpen) {
        this.infoOpen = infoOpen;
    }

    public synchronized void close() {
        this.info.close();

        this.info = null;
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

        this.init();

        this.funcExecute.accept();
    }
}
