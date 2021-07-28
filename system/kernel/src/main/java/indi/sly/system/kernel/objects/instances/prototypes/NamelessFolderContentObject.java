package indi.sly.system.kernel.objects.instances.prototypes;

import javax.inject.Named;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NamelessFolderContentObject extends AInfoContentObject {
    @Override
    protected void read(byte[] source) {
    }

    @Override
    protected byte[] write() {
        return null;
    }
}
