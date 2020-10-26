package indi.sly.system.kernel.objects.entities;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

import indi.sly.system.common.support.ISerializable;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.StringUtils;
import indi.sly.system.common.utility.UUIDUtils;

public class InfoSummaryDefinition implements ISerializable, Comparable<InfoSummaryDefinition> {
    private static final long serialVersionUID = 7257375963605458833L;

    private UUID id;
    private UUID type;
    private String name;

    public UUID getID() {
        return id;
    }

    public void setID(UUID id) {
        this.id = id;
    }

    public UUID getType() {
        return type;
    }

    public void setType(UUID type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        this.id = UUIDUtils.readExternal(in);
        this.type = UUIDUtils.readExternal(in);
        this.name = StringUtils.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        UUIDUtils.writeExternal(out, this.id);
        UUIDUtils.writeExternal(out, this.type);
        StringUtils.writeExternal(out, this.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        InfoSummaryDefinition other = (InfoSummaryDefinition) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public int compareTo(InfoSummaryDefinition other) {
        if (!StringUtils.isNameIllegal(this.name, other.name)) {
            return this.name.compareTo(other.name);
        } else if (ObjectUtils.allNotNull(this.id, other.id)) {
            return this.id.compareTo(other.id);
        } else if (ObjectUtils.allNotNull(this.type, other.type)) {
            return this.type.compareTo(other.type);
        } else {
            return 0;
        }
    }
}
