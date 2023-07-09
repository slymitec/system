package indi.sly.system.kernel.files.instances.prototypes;

import indi.sly.system.common.lang.ConditionRefuseException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
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
public class FileSystemFolderContentObject extends AInfoContentObject {
    public FileSystemFolderContentObject() {
        this.funcCustomRead = () -> this.entry = ObjectUtil.transferFromByteArray(this.value);
        this.funcCustomWrite = () -> this.value = ObjectUtil.transferToByteArray(this.entry);
    }

    private FileSystemEntryDefinition entry;

    public long getType() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivileges(PrivilegeType.FILE_SYSTEM_ACCESS_MODIFY_MAPPING)) {
            throw new ConditionRefuseException();
        }

        try {
            this.lock(LockType.READ);
            this.init();

            return this.entry.getType();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void setType(long type) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivileges(PrivilegeType.FILE_SYSTEM_ACCESS_MODIFY_MAPPING)) {
            throw new ConditionRefuseException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.entry.setType(type);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public byte[] getValue() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivileges(PrivilegeType.FILE_SYSTEM_ACCESS_MODIFY_MAPPING)) {
            throw new ConditionRefuseException();
        }

        try {
            this.lock(LockType.READ);
            this.init();

            return this.entry.getValue();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void setValue(byte[] configuration) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();
        ProcessTokenObject processToken = process.getToken();

        if (!processToken.isPrivileges(PrivilegeType.FILE_SYSTEM_ACCESS_MODIFY_MAPPING)) {
            throw new ConditionRefuseException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            this.entry.setValue(configuration);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
