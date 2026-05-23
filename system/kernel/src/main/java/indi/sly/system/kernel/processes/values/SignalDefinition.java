package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.ADefinition;

import java.util.*;

public class SignalDefinition extends ADefinition {
    public SignalDefinition() {
        this.sourceProcessIds = new HashSet<>();
        this.signalEntries = new ArrayList<>();
    }

    private UUID processId;
    private final Set<UUID> sourceProcessIds;
    private final List<SignalEntryDefinition> signalEntries;
    private int limit;

    public UUID getProcessId() {
        return this.processId;
    }

    public void setProcessId(UUID processId) {
        this.processId = processId;
    }

    public Set<UUID> getSourceProcessIds() {
        return this.sourceProcessIds;
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
        if (o == null || getClass() != o.getClass()) return false;
        SignalDefinition that = (SignalDefinition) o;
        return limit == that.limit && Objects.equals(processId, that.processId) && Objects.equals(sourceProcessIds, that.sourceProcessIds) && Objects.equals(signalEntries, that.signalEntries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processId, sourceProcessIds, signalEntries, limit);
    }
}
