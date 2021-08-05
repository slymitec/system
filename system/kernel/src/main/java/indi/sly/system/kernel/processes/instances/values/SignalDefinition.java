package indi.sly.system.kernel.processes.instances.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class SignalDefinition extends ADefinition<SignalDefinition> {
    public SignalDefinition() {
        this.sourceProcessIDs = new HashSet<>();
        this.signalEntries = new ArrayList<>();
    }

    private UUID processID;
    private final Set<UUID> sourceProcessIDs;
    private final List<SignalEntryDefinition> signalEntries;
    private int limit;

    public UUID getProcessID() {
        return this.processID;
    }

    public void setProcessID(UUID processID) {
        this.processID = processID;
    }

    public Set<UUID> getSourceProcessIDs() {
        return this.sourceProcessIDs;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int size() {
        return this.signalEntries.size();
    }

    public boolean isEmpty() {
        return this.signalEntries.isEmpty();
    }

    public List<SignalEntryDefinition> pollAll() {
        List<SignalEntryDefinition> resultSignalEntries = new ArrayList<>(this.signalEntries);

        this.signalEntries.clear();

        return resultSignalEntries;
    }

    public void add(SignalEntryDefinition signalEntry) {
        if (ObjectUtil.isAnyNull(signalEntry)) {
            throw new ConditionParametersException();
        }

        this.signalEntries.add(signalEntry);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignalDefinition that = (SignalDefinition) o;
        return limit == that.limit &&
                Objects.equals(processID, that.processID) &&
                sourceProcessIDs.equals(that.sourceProcessIDs) &&
                signalEntries.equals(that.signalEntries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processID, sourceProcessIDs, signalEntries, limit);
    }

    @Override
    public SignalDefinition deepClone() {
        SignalDefinition signal = new SignalDefinition();

        signal.processID = this.processID;
        signal.sourceProcessIDs.addAll(this.sourceProcessIDs);
        for (SignalEntryDefinition signalEntry : this.signalEntries) {
            signal.signalEntries.add(signalEntry.deepClone());
        }
        signal.limit = this.limit;

        return signal;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.processID = UUIDUtil.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.sourceProcessIDs.add(UUIDUtil.readExternal(in));
        }

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.signalEntries.add(ObjectUtil.readExternal(in));
        }

        this.limit = NumberUtil.readExternalInteger(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtil.writeExternal(out, this.processID);

        NumberUtil.writeExternalInteger(out, this.sourceProcessIDs.size());
        for (UUID pair : this.sourceProcessIDs) {
            UUIDUtil.writeExternal(out, pair);
        }

        NumberUtil.writeExternalInteger(out, this.signalEntries.size());
        for (SignalEntryDefinition pair : this.signalEntries) {
            ObjectUtil.writeExternal(out, pair);
        }

        NumberUtil.writeExternalLong(out, this.limit);
    }
}
