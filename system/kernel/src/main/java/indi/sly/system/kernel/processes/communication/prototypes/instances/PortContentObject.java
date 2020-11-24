package indi.sly.system.kernel.processes.communication.prototypes.instances;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.common.exceptions.StatusInsufficientResourcesException;
import indi.sly.system.common.types.LockTypes;
import indi.sly.system.common.utility.ArrayUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.prototypes.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.SpaceTypes;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PortContentObject extends AInfoContentObject {
    @Override
    protected void read(byte[] source) {
        this.port = ObjectUtils.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtils.transferToByteArray(this.port);
    }

    private PortDefinition port;

    public byte[] receive() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrentProcess();

        if (!this.port.getProcessID().equals(process.getID())) {
            throw new ConditionPermissionsException();
        }


        this.lock(LockTypes.WRITE);
        this.init();

        byte[] value = this.port.getValue();
        this.port.setValue(ArrayUtils.EMPTY_BYTES);

        this.fresh();
        this.lock(LockTypes.NONE);

        return value;
    }

    public void send(byte[] value) {
        if (ObjectUtils.isAnyNull(value)) {
            throw new ConditionParametersException();
        }

        if (this.port.size() + value.length >= this.port.getLimit()) {
            throw new StatusInsufficientResourcesException();
        }
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrentProcess();

        if (!this.port.getProcessID().equals(process.getID()) && this.port.getSourceProcessIDs().contains(process.getID())) {
            throw new ConditionPermissionsException();
        }

        this.lock(LockTypes.WRITE);
        this.init();

        this.port.setValue(ArrayUtils.combineBytes(this.port.getValue(), value));

        this.fresh();
        this.lock(LockTypes.NONE);
    }
}
