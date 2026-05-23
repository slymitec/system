package indi.sly.system.kernel.security.values;

import indi.sly.system.kernel.core.values.ACacheEntity;
import org.redisson.api.annotation.REntity;

import java.util.Objects;
import java.util.UUID;

@REntity
public class GroupCacheEntity extends ACacheEntity {
    private UUID groupId;

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GroupCacheEntity that = (GroupCacheEntity) o;
        return Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), groupId);
    }
}
