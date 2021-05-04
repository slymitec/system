package indi.sly.system.kernel.processes.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.lang.StatusInsufficientResourcesException;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.instances.values.SignalDefinition;
import indi.sly.system.kernel.processes.instances.values.SignalEntryDefinition;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SignalContentObject extends AInfoContentObject {
    @Override
    protected void read(byte[] source) {
        this.signal = ObjectUtil.transferFromByteArray(source);
    }

    @Override
    protected byte[] write() {
        return ObjectUtil.transferToByteArray(this.signal);
    }

    private SignalDefinition signal;

    public Set<UUID> getSourceProcessIDs() {
        this.init();

        return Collections.unmodifiableSet(this.signal.getSourceProcessIDs());
    }

    public void setSourceProcessIDs(Set<UUID> sourceProcessIDs) {
        if (ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrentProcess();

        if (!this.signal.getProcessID().equals(process.getID())) {
            throw new ConditionPermissionsException();
        }

        Set<UUID> signalSourceProcessIDs = this.signal.getSourceProcessIDs();
        signalSourceProcessIDs.clear();
        signalSourceProcessIDs.addAll(sourceProcessIDs);
    }

    public List<SignalEntryDefinition> receive() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrentProcess();

        if (!this.signal.getProcessID().equals(process.getID())) {
            throw new ConditionPermissionsException();
        }

        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        this.lock(LockType.WRITE);
        this.init();

        List<SignalEntryDefinition> signalEntries = this.signal.pollAll();

        this.fresh();
        this.lock(LockType.NONE);

        for (SignalEntryDefinition signalEntry : signalEntries) {
            signalEntry.getDate().put(DateTimeTypes.ACCESS, nowDateTime);
        }

        return signalEntries;
    }

    public void send(long key, long value) {
        if (this.signal.size() >= this.signal.getLimit()) {
            throw new StatusInsufficientResourcesException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrentProcess();

        if (!this.signal.getProcessID().equals(process.getID()) && this.signal.getSourceProcessIDs().contains(process.getID())) {
            throw new ConditionPermissionsException();
        }

        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        SignalEntryDefinition signalEntry = new SignalEntryDefinition();
        signalEntry.setSource(process.getID());
        signalEntry.setKey(key);
        signalEntry.setValue(value);
        signalEntry.getDate().put(DateTimeTypes.CREATE, nowDateTime);
        signalEntry.getDate().put(DateTimeTypes.ACCESS, nowDateTime);

        this.lock(LockType.WRITE);
        this.init();

        this.signal.add(signalEntry);

        this.fresh();
        this.lock(LockType.NONE);
    }
}
