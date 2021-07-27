package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.ACoreProcessPrototype;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AInfoContentObject extends ACoreProcessPrototype<byte[]> {
    private Consumer funcExecute;

    protected InfoObject info;
    protected InfoOpenDefinition open;

    public final void setExecute(Consumer funcExecute) {
        if (ObjectUtil.isAnyNull(funcExecute)) {
            throw new ConditionParametersException();
        }

        this.funcExecute = funcExecute;
    }

    public void setInfo(InfoObject info) {
        this.info = info;
    }

    public void setOpen(InfoOpenDefinition open) {
        this.open = open;
    }

    public synchronized void close() {
        this.info.close();

        this.info = null;
        this.open = null;
        this.setParent(null);
        this.setSource(() -> {
            throw new StatusRelationshipErrorException();
        }, (value) -> {
            throw new StatusRelationshipErrorException();
        });
        this.setLock((lock) -> {
            throw new StatusRelationshipErrorException();
        });
    }

    public synchronized void execute() {
        if (ObjectUtil.isAnyNull(this.funcExecute)) {
            throw new StatusNotSupportedException();
        }

        this.init();

        this.funcExecute.accept();
    }
}
