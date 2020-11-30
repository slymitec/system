package indi.sly.system.kernel.objects.instances.prototypes;

import javax.inject.Named;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FolderContentObject extends AInfoContentObject {
    @Override
    protected void read(byte[] value) {
    }

    @Override
    protected byte[] write() {
        return null;
    }
}
