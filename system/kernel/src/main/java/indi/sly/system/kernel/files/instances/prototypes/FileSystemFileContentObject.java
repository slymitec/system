package indi.sly.system.kernel.files.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.lang.StatusUnexpectedException;
import indi.sly.system.common.supports.ArrayUtil;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.prototypes.IByteValueSupporter;
import indi.sly.system.kernel.files.instances.values.FileSystemEntryDefinition;
import indi.sly.system.kernel.files.instances.values.FileSystemLocationType;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileSystemFileContentObject extends AInfoContentObject implements IByteValueSupporter<FileSystemEntryDefinition> {
    public long length() {
        FileSystemEntryDefinition fileSystemEntry = this.init(this.read());

        long length = -1;

        if (LogicalUtil.isAllExist(fileSystemEntry.getType(), FileSystemLocationType.REPOSITORY)) {
            length = fileSystemEntry.getValue().length;
        } else if (LogicalUtil.isAllExist(fileSystemEntry.getType(), FileSystemLocationType.MAPPING)) {
            File infoFile = new File(StringUtil.readFormBytes(fileSystemEntry.getValue()));

            if (!infoFile.exists() || !infoFile.isFile()) {
                throw new StatusNotExistedException();
            }

            length = infoFile.length();
        }

        return length;
    }

    public byte[] read(long offset, int length) {
        if (length < 0 || offset + length > this.length()) {
            throw new ConditionParametersException();
        }

        byte[] value = null;

        FileSystemEntryDefinition fileSystemEntry = this.init(this.read());

        if (LogicalUtil.isAllExist(fileSystemEntry.getType(), FileSystemLocationType.REPOSITORY)) {
            if (offset + length > Integer.MAX_VALUE) {
                throw new ConditionParametersException();
            }

            value = ArrayUtil.acquireBytes(fileSystemEntry.getValue(), (int) offset, length);
        } else if (LogicalUtil.isAllExist(fileSystemEntry.getType(), FileSystemLocationType.MAPPING)) {
            value = new byte[length];

            File infoFile = new File(StringUtil.readFormBytes(fileSystemEntry.getValue()));

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

        this.write(this.flush(fileSystemEntry));

        return value;
    }

    public void write(byte[] value) {
        if (ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        FileSystemEntryDefinition fileSystemEntry = this.init(this.read());

        if (LogicalUtil.isAllExist(fileSystemEntry.getType(), FileSystemLocationType.REPOSITORY)) {
            fileSystemEntry.setValue(value);
        } else if (LogicalUtil.isAllExist(fileSystemEntry.getType(), FileSystemLocationType.MAPPING)) {
            File infoFile = new File(StringUtil.readFormBytes(fileSystemEntry.getValue()));

            if (!infoFile.exists() || !infoFile.isFile()) {
                throw new StatusNotExistedException();
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream(infoFile, false)) {
                fileOutputStream.write(value);
            } catch (IOException e) {
                throw new StatusUnexpectedException();
            }
        }

        this.write(this.flush(fileSystemEntry));
    }

    public void append(byte[] value) {
        if (ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        FileSystemEntryDefinition fileSystemEntry = this.init(this.read());

        if (LogicalUtil.isAllExist(fileSystemEntry.getType(), FileSystemLocationType.REPOSITORY)) {
            if (this.length() + value.length > Integer.MAX_VALUE) {
                throw new ConditionParametersException();
            }

            fileSystemEntry.setValue(ArrayUtil.combineBytes(fileSystemEntry.getValue(), value));
        } else if (LogicalUtil.isAllExist(fileSystemEntry.getType(), FileSystemLocationType.MAPPING)) {
            File infoFile = new File(StringUtil.readFormBytes(fileSystemEntry.getValue()));

            if (!infoFile.exists() || !infoFile.isFile()) {
                throw new StatusNotExistedException();
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream(infoFile, true)) {
                fileOutputStream.write(value);
            } catch (IOException e) {
                throw new StatusUnexpectedException();
            }
        }

        this.write(this.flush(fileSystemEntry));
    }

    public void clear() {
        this.write(ArrayUtil.EMPTY_BYTES);
    }
}
