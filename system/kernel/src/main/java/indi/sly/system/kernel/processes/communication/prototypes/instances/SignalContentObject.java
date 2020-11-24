package indi.sly.system.kernel.processes.communication.prototypes.instances;

import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.common.exceptions.StatusInsufficientResourcesException;
import indi.sly.system.common.types.LockTypes;
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
public class SignalContentObject extends AInfoContentObject {
    @Override
    protected void read(byte[] source) {
        this.signals = ObjectUtils.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtils.transferToByteArray(this.signals);
    }

    private SignalDefinition signals;

    //自己能建读写删，别人能写

    public List<SignalEntryDefinition> get() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrentProcess();

        if (!this.signals.getProcessID().equals(process.getID())) {
            throw new ConditionPermissionsException();
        }

        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().get(SpaceTypes.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        List<SignalEntryDefinition> signalEntries = this.signals.pollAll();
        for (SignalEntryDefinition signalEntry : signalEntries) {
            signalEntry.getDate().put(DateTimeTypes.ACCESS, nowDateTime);
        }

        return signalEntries;
    }


    public void send(long value) {
        if (this.signals.size() >= this.signals.getMaxSignalsCount()) {
            throw new StatusInsufficientResourcesException();
        }
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrentProcess();

        if (!this.signals.getProcessID().equals(process.getID()) && this.signals.getSourceProcessIDs().contains(process.getID())) {
            throw new ConditionPermissionsException();
        }

        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().get(SpaceTypes.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        SignalEntryDefinition signalEntry = new SignalEntryDefinition();
        signalEntry.setSource(process.getID());
        signalEntry.setValue(value);
        signalEntry.setStatus(SignalStatuesTypes.UNREAD);
        signalEntry.getDate().put(DateTimeTypes.CREATE, nowDateTime);
        signalEntry.getDate().put(DateTimeTypes.ACCESS, nowDateTime);

        this.lock(LockTypes.WRITE);
        this.init();

        this.signals.add(signalEntry);

        this.fresh();
        this.lock(LockTypes.NONE);
    }


    //....
}
