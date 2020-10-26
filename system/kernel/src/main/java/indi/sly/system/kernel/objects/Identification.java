package indi.sly.system.kernel.objects;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.UUID;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.support.IDeepCloneable;
import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.ClassUtils;
import indi.sly.system.common.utility.NumberUtils;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.StringUtils;
import indi.sly.system.common.utility.UUIDUtils;

public final class Identification implements IDeepCloneable<Identification>, ISerializable {
    private byte[] id;
    private Class<?> type;

    public byte[] getID() {
        return this.id;
    }

    public Class<?> getType() {
        return this.type;
    }

    public Identification() {
        this.id = UUIDUtils.writeToBytes(UUIDUtils.getEmpty());
        this.type = UUID.class;
    }

    public Identification(UUID id) {
        if (ObjectUtils.isAnyNull(id)) {
            throw new ConditionParametersException();
        }

        this.id = UUIDUtils.writeToBytes(id);
        this.type = UUID.class;
    }

    public Identification(String id) {
        if (StringUtils.isNameIllegal(id)) {
            throw new ConditionParametersException();
        }

        this.id = StringUtils.writeToBytes(id);
        this.type = String.class;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Identification other = (Identification) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (!Arrays.equals(id, other.id))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + Arrays.hashCode(id);
        return result;
    }

    @Override
    public String toString() {
        return ClassUtils.getSimpleName(Identification.class);
    }

    @Override
    public Object clone() {
        return this.deepClone();
    }

    @Override
    public Identification deepClone() {
        Identification newObject = new Identification();

        newObject.id = this.id == null ? null : new byte[this.id.length];
        System.arraycopy(this.id, 0, newObject.id, 0, this.id.length);

        newObject.type = this.type;

        return newObject;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = NumberUtils.readExternalBytes(in);
        this.type = ClassUtils.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        NumberUtils.writeExternalBytes(out, this.id);
        ClassUtils.writeExternal(out, this.type);
    }
}
