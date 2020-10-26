package indi.sly.system.kernel.processes.prototypes.instances;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;

public class HandleTableDefinition implements ISerializable {
    private final Map<UUID, HandleEntryDefinition> table;

    public HandleTableDefinition() {
        this.table = new Hashtable<>();
    }

    public Map<UUID, HandleEntryDefinition> getTable() {
        return this.table;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int valueInteger;

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.table.put(UUIDUtils.readExternal(in), ObjectUtils.readExternal(in));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtils.writeExternalInteger(out, this.table.size());
        for (Entry<UUID, HandleEntryDefinition> pair : this.table.entrySet()) {
            UUIDUtils.writeExternal(out, pair.getKey());
            ObjectUtils.writeExternal(out, pair.getValue());
        }
    }
}
