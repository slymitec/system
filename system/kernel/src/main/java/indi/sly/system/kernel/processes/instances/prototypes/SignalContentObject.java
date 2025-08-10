package indi.sly.system.kernel.processes.instances.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.MethodScope;
import indi.sly.system.common.lang.StatusInsufficientResourcesException;
import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.common.values.MethodScopeType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.values.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
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
public class SignalContentObject extends AInfoContentObject {
    public SignalContentObject() {
        this.funcCustomRead = () -> this.signal = ObjectUtil.transferFromByteArray(this.value);
        this.funcCustomWrite = () -> this.value = ObjectUtil.transferToByteArray(this.signal);
    }

    private SignalDefinition signal;

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public Set<UUID> getSourceProcessIDs() {
        try {
            this.lock(LockType.WRITE);
            this.init();

            return CollectionUtil.unmodifiable(this.signal.getSourceProcessIDs());
        } finally {
            this.unlock(LockType.WRITE);
        }
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public void setSourceProcessIDs(Set<UUID> sourceProcessIDs) {
        if (ObjectUtil.isAnyNull(sourceProcessIDs)) {
            throw new ConditionParametersException();
        }

        try {
            this.lock(LockType.WRITE);
            this.init();

            Set<UUID> signalSourceProcessIDs = this.signal.getSourceProcessIDs();
            signalSourceProcessIDs.clear();
            signalSourceProcessIDs.addAll(sourceProcessIDs);

            this.fresh();
        } finally {
            this.unlock(LockType.WRITE);
        }
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public long getLimit() {
        try {
            this.lock(LockType.WRITE);
            this.init();

            return this.signal.getLimit();
        } finally {
            this.unlock(LockType.WRITE);
        }
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public List<SignalEntryDefinition> receive() {
        List<SignalEntryDefinition> signalEntries;

        try {
            this.lock(LockType.WRITE);
            this.init();

            DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            signalEntries = this.signal.pollAll();

            this.fresh();

            for (SignalEntryDefinition signalEntry : signalEntries) {
                signalEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);
            }
        } finally {
            this.unlock(LockType.WRITE);
        }

        return CollectionUtil.unmodifiable(signalEntries);
    }

    @MethodScope(value = MethodScopeType.ONLY_KERNEL)
    public void send(long key, long value) {
        try {
            this.lock(LockType.WRITE);
            this.init();

            if (this.signal.size() >= this.signal.getLimit()) {
                throw new StatusInsufficientResourcesException();
            }

            ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
            ProcessObject process = processManager.getCurrent();

            DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);
            long nowDateTime = dateTime.getCurrentDateTime();

            SignalEntryDefinition signalEntry = new SignalEntryDefinition();
            signalEntry.setSource(process.getID());
            signalEntry.setKey(key);
            signalEntry.setValue(value);
            signalEntry.getDate().put(DateTimeType.CREATE, nowDateTime);
            signalEntry.getDate().put(DateTimeType.ACCESS, nowDateTime);

            this.signal.add(signalEntry);

            this.fresh();
        } finally {
            this.unlock(LockType.WRITE);
        }
    }
}
