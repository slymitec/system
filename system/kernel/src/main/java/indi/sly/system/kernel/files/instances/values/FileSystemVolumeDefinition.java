package indi.sly.system.kernel.files.instances.values;

import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class FileSystemVolumeDefinition extends ADefinition<FileSystemVolumeDefinition> {
    public FileSystemVolumeDefinition() {
    }


    @Override
    public FileSystemVolumeDefinition deepClone() {
        FileSystemVolumeDefinition definition = new FileSystemVolumeDefinition();

        return definition;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
    }
}
