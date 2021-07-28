package indi.sly.system.kernel.files.instances.prototypes;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.files.instances.values.FileSystemFileDefinition;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileSystemFileContentObject extends AInfoContentObject {
    @Override
    protected void read(byte[] source) {
        this.file = ObjectUtil.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtil.transferToByteArray(this.file);
    }

    private FileSystemFileDefinition file;

    public byte[] getValue() {
        this.init();

        return this.file.getValue();
    }

    public void setValue(byte[] value) {
        try {
            this.lock(LockType.WRITE);
            this.init();

            this.file.setValue(value);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
