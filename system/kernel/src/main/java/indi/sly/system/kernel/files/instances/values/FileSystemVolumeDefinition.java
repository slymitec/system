package indi.sly.system.kernel.files.instances.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class FileSystemVolumeDefinition extends ADefinition<FileSystemVolumeDefinition> {
    public FileSystemVolumeDefinition() {
    }

    private long type;
    private byte[] configuration;

    public long getType() {
        return this.type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public byte[] getConfiguration() {
        return this.configuration;
    }

    public void setConfiguration(byte[] configuration) {
        this.configuration = configuration;
    }

    @Override
    public FileSystemVolumeDefinition deepClone() {
        FileSystemVolumeDefinition definition = new FileSystemVolumeDefinition();

        definition.type = this.type;
        definition.configuration = this.configuration;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.type = NumberUtil.readExternalLong(in);
        this.configuration = NumberUtil.readExternalBytes(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtil.writeExternalLong(out, this.type);
        NumberUtil.writeExternalBytes(out, this.configuration);
    }
}
