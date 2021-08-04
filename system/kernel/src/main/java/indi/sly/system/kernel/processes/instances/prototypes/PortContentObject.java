package indi.sly.system.kernel.processes.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.lang.StatusInsufficientResourcesException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.ArrayUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.instances.values.PortDefinition;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PortContentObject extends AInfoContentObject {
    @Override
    protected void read(byte[] source) {
        this.port = ObjectUtil.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtil.transferToByteArray(this.port);
    }

    private PortDefinition port;

    public Set<UUID> getSourceProcessIDs() {
        this.init();

        return CollectionUtil.unmodifiable(this.port.getSourceProcessIDs());
    }

    public void setSourceProcessIDs(Set<UUID> sourceProcessIDs) {
        if (ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!this.port.getProcessID().equals(process.getID())) {
                throw new ConditionPermissionsException();
            }

            Set<UUID> portSourceProcessIDs = this.port.getSourceProcessIDs();
            portSourceProcessIDs.clear();
            portSourceProcessIDs.addAll(sourceProcessIDs);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public byte[] receive() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();

        byte[] value = null;

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (!this.port.getProcessID().equals(process.getID())) {
                throw new ConditionPermissionsException();
            }

            value = this.port.getValue();
            this.port.setValue(ArrayUtil.EMPTY_BYTES);

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }

        return value;
    }

    public void send(byte[] value) {
        if (ObjectUtil.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            if (this.port.size() + value.length >= this.port.getLimit()) {
                throw new StatusInsufficientResourcesException();
            }

            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();

            if (!this.port.getProcessID().equals(process.getID()) && this.port.getSourceProcessIDs().contains(process.getID())) {
                throw new ConditionPermissionsException();
            }

            this.port.setValue(ArrayUtil.combineBytes(this.port.getValue(), value));

            this.fresh();
        } finally {
            this.lock(LockType.NONE);
        }
    }
}
