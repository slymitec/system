package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.ADefinition;
import indi.sly.system.kernel.core.values.ACacheEntity;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RIndex;

import java.util.*;

@REntity
public class SignalCacheEntity extends ACacheEntity {
    public SignalCacheEntity() {
        this.sourceProcessIds = new HashSet<>();
        this.signalEntries = new ArrayList<>();
    }

    private final Set<UUID> sourceProcessIds;
    private final List<SignalEntryDefinition> signalEntries;
    private int limit;

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
        if (!(o instanceof SignalCacheEntity cache)) return false;
        return Objects.equals(id, cache.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}