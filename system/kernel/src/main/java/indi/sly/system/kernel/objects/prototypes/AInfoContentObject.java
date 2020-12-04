package indi.sly.system.kernel.objects.prototypes;

import indi.sly.system.kernel.core.prototypes.ABytesProcessPrototype;
import indi.sly.system.kernel.objects.values.InfoStatusOpenDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AInfoContentObject extends ABytesProcessPrototype {
    protected InfoStatusOpenDefinition statusOpen;

    public void setStatusOpen(InfoStatusOpenDefinition statusOpen) {
        this.statusOpen = statusOpen;
    }
}
