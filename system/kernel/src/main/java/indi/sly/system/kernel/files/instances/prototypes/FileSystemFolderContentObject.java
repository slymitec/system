package indi.sly.system.kernel.files.instances.prototypes;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.files.instances.values.FileSystemEntryDefinition;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileSystemFolderContentObject extends AInfoContentObject {
    @Override
    protected void read(byte[] source) {
        this.fileSystemVolume = ObjectUtil.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtil.transferToByteArray(this.fileSystemVolume);
    }

    private FileSystemEntryDefinition fileSystemVolume;

    public long getType() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.fileSystemVolume.getType();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void setType(long type) {
        try {
            this.lock(LockType.WRITE);
            this.init();

            this.fileSystemVolume.setType(type);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public byte[] getConfiguration() {
        try {
            this.lock(LockType.READ);
            this.init();

            return fileSystemVolume.getValue();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void setConfiguration(byte[] configuration) {
        try {
            this.lock(LockType.WRITE);
            this.init();

            fileSystemVolume.setValue(configuration);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
