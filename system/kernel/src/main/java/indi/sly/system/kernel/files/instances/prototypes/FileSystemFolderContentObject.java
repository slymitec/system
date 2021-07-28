package indi.sly.system.kernel.files.instances.prototypes;

import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileSystemFolderContentObject extends AInfoContentObject {
    @Override
    protected void read(byte[] value) {
    }

    @Override
    protected byte[] write() {
        return null;
    }
}
