package indi.sly.system.kernel.processes.prototypes.instances;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.objects.prototypes.StatusDefinition;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class HandleEntryDefinition implements ISerializable {
    private final Map<Long, Date> date;
    private StatusDefinition status;
    private UUID transferProcessID;

    public HandleEntryDefinition() {
        this.date = new HashMap<>();
        this.transferProcessID = UUIDUtils.getEmpty();
    }

    public Map<Long, Date> getDate() {
        return this.date;
    }

    public StatusDefinition getStatus() {
        return this.status;
    }

    public void setStatus(StatusDefinition status) {
        this.status = status;
    }

    public UUID getTransferProcessID() {
        return this.transferProcessID;
    }

    public void setTransferProcessID(UUID transferProcessID) {
        this.transferProcessID = transferProcessID;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int valueInteger;

        valueInteger = NumberUtils.readExternalInteger(in);
        for (int i = 0; i < valueInteger; i++) {
            this.date.put(NumberUtils.readExternalLong(in), new Date(NumberUtils.readExternalLong(in)));
        }
        this.status = ObjectUtils.readExternal(in);
        this.transferProcessID = UUIDUtils.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        for (Entry<Long, Date> pair : this.date.entrySet()) {
            NumberUtils.writeExternalLong(out, pair.getKey());
            NumberUtils.writeExternalLong(out, pair.getValue().getTime());
        }
        ObjectUtils.writeExternal(out, this.status);
        UUIDUtils.writeExternal(out, this.transferProcessID);
    }
}
