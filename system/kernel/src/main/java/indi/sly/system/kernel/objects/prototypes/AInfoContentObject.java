package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.kernel.core.prototypes.ACoreProcessPrototype;
import indi.sly.system.kernel.objects.values.InfoStatusOpenDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AInfoContentObject extends ACoreProcessPrototype<byte[]> {
    protected InfoObject info;
    protected InfoStatusOpenDefinition statusOpen;

    public void setInfo(InfoObject info) {
        this.info = info;
    }

    public void setStatusOpen(InfoStatusOpenDefinition statusOpen) {
        this.statusOpen = statusOpen;
    }

    public synchronized void close() {
        this.info.close();

        this.info = null;
        this.statusOpen = null;
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
}
