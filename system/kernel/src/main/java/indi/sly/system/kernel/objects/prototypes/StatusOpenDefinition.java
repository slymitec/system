package indi.sly.system.kernel.objects.prototypes;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.ObjectUtils;

public class StatusOpenDefinition implements ISerializable {
    private long attribute;
    private ISerializable context;

    public long getAttribute() {
        return this.attribute;
    }

    public void setAttribute(long openAttribute) {
        this.attribute = openAttribute;
    }

    public ISerializable getContext() {
        return this.context;
    }

    public void setContext(ISerializable context) {
        this.context = context;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.attribute = NumberUtils.readExternalLong(in);
        this.context = ObjectUtils.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtils.writeExternalLong(out, this.attribute);
        ObjectUtils.writeExternal(out, this.context);
    }
}
