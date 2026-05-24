package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.values.ADefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SignalEntryDefinition extends ADefinition {
    public SignalEntryDefinition() {
        this.date = new HashMap<>();
    }

    private UUID source;
    private long key;
    private long value;
    private final Map<Long, Long> date;

    public UUID getSource() {
        return this.source;
    }

    public void setSource(UUID source) {
        this.source = source;
    }

    public long getKey() {
        return this.key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public long getValue() {
        return this.value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public Map<Long, Long> getDate() {
        return this.date;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof SignalEntryDefinition that)) return false;
        return key == that.key && value == that.value && Objects.equals(source, that.source) && Objects.equals(date, that.date);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(source, key, value, date);
    }
}
