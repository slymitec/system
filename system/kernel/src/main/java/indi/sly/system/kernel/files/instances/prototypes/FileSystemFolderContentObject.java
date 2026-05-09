package indi.sly.system.kernel.files.instances.prototypes;

import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.IByteValueProcess;
import indi.sly.system.kernel.files.instances.values.FileSystemEntryDefinition;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileSystemFolderContentObject extends AInfoContentObject implements IByteValueProcess<FileSystemEntryDefinition> {
    public long getType() {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivileges(PrivilegeType.FILE_SYSTEM_ACCESS_MODIFY_MAPPING)) {
            throw new ConditionRefuseException();
        }

        FileSystemEntryDefinition fileSystemEntry = this.init(this.read());

        return fileSystemEntry.getType();
    }

    public void setType(long type) {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivileges(PrivilegeType.FILE_SYSTEM_ACCESS_MODIFY_MAPPING)) {
            throw new ConditionRefuseException();
        }

        FileSystemEntryDefinition fileSystemEntry = this.init(this.read());

        fileSystemEntry.setType(type);

        this.write(this.flush(fileSystemEntry));
    }

    public byte[] getValue() {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivileges(PrivilegeType.FILE_SYSTEM_ACCESS_MODIFY_MAPPING)) {
            throw new ConditionRefuseException();
        }

        FileSystemEntryDefinition fileSystemEntry = this.init(this.read());

        return fileSystemEntry.getValue();
    }

    public void setValue(byte[] configuration) {
        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivileges(PrivilegeType.FILE_SYSTEM_ACCESS_MODIFY_MAPPING)) {
            throw new ConditionRefuseException();
        }

        FileSystemEntryDefinition fileSystemEntry = this.init(this.read());

        fileSystemEntry.setValue(configuration);

        this.write(this.flush(fileSystemEntry));
    }
}
