package indi.sly.system.kernel.processes.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusInsufficientResourcesException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.prototypes.IByteValueSupporter;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.instances.values.SignalDefinition;
import indi.sly.system.kernel.processes.instances.values.SignalEntryDefinition;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SignalContentObject extends AInfoContentObject implements IByteValueSupporter<SignalDefinition> {
    public Set<UUID> getSourceProcessIDs() {
        SignalDefinition signal = this.init(this.read());

        return CollectionUtil.unmodifiable(signal.getSourceProcessIDs());
    }

    public void setSourceProcessIDs(Set<UUID> sourceProcessIDs) {
        if (ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        SignalDefinition signal = this.init(this.read());

        Set<UUID> signalSourceProcessIDs = signal.getSourceProcessIDs();
        signalSourceProcessIDs.clear();
        signalSourceProcessIDs.addAll(sourceProcessIDs);

        this.write(this.flush(signal));
    }

    public long getLimit() {
        SignalDefinition signal = this.init(this.read());

        return signal.getLimit();
    }

    public List<SignalEntryDefinition> receive() {
        SignalDefinition signal = this.init(this.read());

        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrent();

        List<SignalEntryDefinition> signalEntries = signal.pollAll();

        this.write(this.flush(signal));

        for (SignalEntryDefinition signalEntry : signalEntries) {
            signalEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);
        }

        return CollectionUtil.unmodifiable(signalEntries);
    }

    public void send(long key, long value) {
        SignalDefinition signal = this.init(this.read());

        if (signal.size() >= signal.getLimit()) {
            throw new StatusInsufficientResourcesException();
        }

        ProcessManager processManager = this.coreManager.getManager(ProcessManager.class);
        ProcessObject process = processManager.getCurrent();

        DateTimeObject dateTime = this.coreManager.getDateTime();
        long nowDateTime = dateTime.getCurrent();

        SignalEntryDefinition signalEntry = new SignalEntryDefinition();
        signalEntry.setSource(process.getId());
        signalEntry.setKey(key);
        signalEntry.setValue(value);
        signalEntry.getDate().put(DateTimeType.CREATE, nowDateTime);
        signalEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

        signal.add(signalEntry);

        this.write(this.flush(signal));
    }
}
