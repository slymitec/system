package indi.sly.system.kernel.files.instances.prototypes;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.files.instances.values.FileSystemVolumeDefinition;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileSystemVolumeContentObject extends AInfoContentObject {
    @Override
    protected void read(byte[] source) {
        this.fileSystemVolume = ObjectUtil.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtil.transferToByteArray(this.fileSystemVolume);
    }

    private FileSystemVolumeDefinition fileSystemVolume;

    public long getType() {
        this.init();

        return this.fileSystemVolume.getType();
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

    public String getPath() {
        this.init();

        return this.fileSystemVolume.getPath();
    }

    public void setPath(String path) {
        try {
            this.lock(LockType.WRITE);
            this.init();

            this.fileSystemVolume.setPath(path);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
