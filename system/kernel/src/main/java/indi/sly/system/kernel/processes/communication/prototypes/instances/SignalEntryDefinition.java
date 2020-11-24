package indi.sly.system.kernel.processes.communication.prototypes.instances;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.UUIDUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SignalEntryDefinition implements ISerializable<SignalEntryDefinition> {
    public SignalEntryDefinition() {
        this.date = new HashMap<>();
    }

    private UUID source;
    private long value;
    private long status;
    private final Map<Long, Long> date;

    public UUID getSource() {
        return this.source;
    }

    public void setSource(UUID source) {
        this.source = source;
    }

    public long getValue() {
        return this.value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getStatus() {
        return this.status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public Map<Long, Long> getDate() {
        return this.date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignalEntryDefinition that = (SignalEntryDefinition) o;
        return value == that.value &&
                status == that.status &&
                Objects.equals(source, that.source) &&
                date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, value, status, date);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return this.deepClone();
    }

    @Override
    public SignalEntryDefinition deepClone() {
        SignalEntryDefinition signal = new SignalEntryDefinition();

        signal.source = this.source;
        signal.value = this.value;
        signal.status = this.status;
        signal.date.putAll(this.date);

        return signal;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.source = UUIDUtils.readExternal(in);
        this.value = NumberUtils.readExternalLong(in);
        this.status = NumberUtils.readExternalLong(in);

        int valueInteger;

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.date.put(NumberUtils.readExternalLong(in), NumberUtils.readExternalLong(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtils.writeExternal(out, this.source);
        NumberUtils.writeExternalLong(out, this.value);
        NumberUtils.writeExternalLong(out, this.status);

        NumberUtils.writeExternalInteger(out, this.date.size());
        for (Map.Entry<Long, Long> pair : this.date.entrySet()) {
            NumberUtils.writeExternalLong(out, pair.getKey());
            NumberUtils.writeExternalLong(out, pair.getValue());
        }
    }
}
