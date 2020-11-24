package indi.sly.system.kernel.processes.communication.prototypes.instances;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class SignalDefinition implements ISerializable<SignalDefinition> {
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

        return Collections.unmodifiableList(resultSignalEntries);
    }

    public void add(SignalEntryDefinition signalEntry) {
        if (ObjectUtils.isAnyNull(signalEntry)) {
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
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public SignalDefinition deepClone() {
        SignalDefinition signal = new SignalDefinition();

        signal.processID = this.processID;
        this.sourceProcessIDs.addAll(this.sourceProcessIDs);
        for (SignalEntryDefinition signalEntry : this.signalEntries) {
            signal.signalEntries.add(signalEntry.deepClone());
        }
        signal.limit = this.limit;

        return signal;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.processID = UUIDUtils.readExternal(in);

        int valueInteger;

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.sourceProcessIDs.add(UUIDUtils.readExternal(in));
        }

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.signalEntries.add(ObjectUtils.readExternal(in));
        }

        this.limit = NumberUtils.readExternalInteger(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtils.writeExternal(out, this.processID);

        NumberUtils.writeExternalInteger(out, this.sourceProcessIDs.size());
        for (UUID pair : this.sourceProcessIDs) {
            UUIDUtils.writeExternal(out, pair);
        }

        NumberUtils.writeExternalInteger(out, this.signalEntries.size());
        for (SignalEntryDefinition pair : this.signalEntries) {
            ObjectUtils.writeExternal(out, pair);
        }

        NumberUtils.writeExternalLong(out, this.limit);
    }
}
