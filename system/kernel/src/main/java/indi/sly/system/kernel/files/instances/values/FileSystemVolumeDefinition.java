package indi.sly.system.kernel.files.instances.values;

import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class FileSystemVolumeDefinition extends ADefinition<FileSystemVolumeDefinition> {
    public FileSystemVolumeDefinition() {
    }

    private long type;
    private String path;

    public long getType() {
        return this.type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public FileSystemVolumeDefinition deepClone() {
        FileSystemVolumeDefinition definition = new FileSystemVolumeDefinition();

        definition.type = this.type;
        definition.path = this.path;

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.type = NumberUtil.readExternalLong(in);
        this.path = StringUtil.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtil.writeExternalLong(out, this.type);
        StringUtil.writeExternal(out, this.path);
    }
}
