package indi.sly.system.kernel.processes.communication.instances.values;

import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.supports.NumberUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.kernel.core.values.ADefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SignalEntryDefinition extends ADefinition<SignalEntryDefinition> {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignalEntryDefinition that = (SignalEntryDefinition) o;
        return key == that.key &&
                value == that.value &&
                Objects.equals(source, that.source) &&
                date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, key, value, date);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public SignalEntryDefinition deepClone() {
        SignalEntryDefinition signal = new SignalEntryDefinition();

        signal.source = this.source;
        signal.key = this.key;
        signal.value = this.value;
        signal.date.putAll(this.date);

        return signal;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.source = UUIDUtil.readExternal(in);
        this.key = NumberUtil.readExternalLong(in);
        this.value = NumberUtil.readExternalLong(in);

        int valueInteger;

        valueInteger = NumberUtil.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.date.put(NumberUtil.readExternalLong(in), NumberUtil.readExternalLong(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtil.writeExternal(out, this.source);
        NumberUtil.writeExternalLong(out, this.key);
        NumberUtil.writeExternalLong(out, this.value);

        NumberUtil.writeExternalInteger(out, this.date.size());
        for (Map.Entry<Long, Long> pair : this.date.entrySet()) {
            NumberUtil.writeExternalLong(out, pair.getKey());
            NumberUtil.writeExternalLong(out, pair.getValue());
        }
    }
}
