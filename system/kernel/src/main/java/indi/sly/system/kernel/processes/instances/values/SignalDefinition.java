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

public class SignalDefinition extends ADefinition {
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
}
