package indi.sly.system.kernel.files.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.supports.ArrayUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.files.instances.values.FileSystemEntryDefinition;
import indi.sly.system.kernel.files.instances.values.FileSystemLocationType;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileSystemFileContentObject extends AInfoContentObject {
    @Override
    protected void read(byte[] source) {
        this.entry = ObjectUtil.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtil.transferToByteArray(this.entry);
    }

    private FileSystemEntryDefinition entry;

    public long length() {
        try {
            this.lock(LockType.READ);
            this.init();

            long length = -1;

            if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.REPOSITORY)) {
                length = this.entry.getValue().length;
            } else if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.MAPPING)) {
                File infoFile = new File(StringUtil.readFormBytes(entry.getValue()));

                if (!infoFile.exists() || !infoFile.isFile()) {
                    throw new StatusNotExistedException();
                }

                length = infoFile.length();
            }

            return length;
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public byte[] read(long offset, int length) {
        if (length < 0 || offset + length >= this.length()) {
            throw new ConditionParametersException();
        }

        byte[] value = null;

        try {
            this.lock(LockType.READ);
            this.init();

            if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.REPOSITORY)) {
                if (offset + length > Integer.MAX_VALUE) {
                    throw new ConditionParametersException();
                }

                value = ArrayUtil.acquireBytes(this.value, (int) offset, length);
            } else if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.MAPPING)) {
                value = new byte[length];

                File infoFile = new File(StringUtil.readFormBytes(entry.getValue()));

                if (!infoFile.exists() || !infoFile.isFile()) {
                    throw new StatusNotExistedException();
                }

                try (FileInputStream fileInputStream = new FileInputStream(infoFile)) {
                    fileInputStream.skip(offset);
                    fileInputStream.read(value);
                } catch (IOException e) {
                    throw new StatusUnexpectedException();
                }
            }
        } finally {
            this.lock(LockType.NONE);
        }

        return value;
    }

    public void write(byte[] value) {
        if (ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        try {
            this.lock(LockType.READ);
            this.init();

            if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.REPOSITORY)) {
                this.entry.setValue(value);
            } else if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.MAPPING)) {
                File infoFile = new File(StringUtil.readFormBytes(entry.getValue()));

                if (!infoFile.exists() || !infoFile.isFile()) {
                    throw new StatusNotExistedException();
                }

                try (FileOutputStream fileOutputStream = new FileOutputStream(infoFile, false)) {
                    fileOutputStream.write(value);
                } catch (IOException e) {
                    throw new StatusUnexpectedException();
                }
            }
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void append(byte[] value) {
        if (ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        try {
            this.lock(LockType.READ);
            this.init();

            if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.REPOSITORY)) {
                if (this.length() + value.length > Integer.MAX_VALUE) {
                    throw new ConditionParametersException();
                }

                this.entry.setValue(ArrayUtil.combineBytes(this.entry.getValue(), value));
            } else if (LogicalUtil.isAllExist(entry.getType(), FileSystemLocationType.MAPPING)) {
                File infoFile = new File(StringUtil.readFormBytes(entry.getValue()));

                if (!infoFile.exists() || !infoFile.isFile()) {
                    throw new StatusNotExistedException();
                }

                try (FileOutputStream fileOutputStream = new FileOutputStream(infoFile, true)) {
                    fileOutputStream.write(value);
                } catch (IOException e) {
                    throw new StatusUnexpectedException();
                }
            }
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void clear() {
        this.write(ArrayUtil.EMPTY_BYTES);
    }
}
