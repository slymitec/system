package indi.sly.system.kernel.security.values;

import indi.sly.system.kernel.core.values.ACacheEntity;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RObjectField;

import java.util.Objects;

@REntity
public class GroupChildCacheEntity extends ACacheEntity {
    @RObjectField
    private GroupCacheEntity group;

    public GroupCacheEntity getGroup() {
        return group;
    }

    public void setGroup(GroupCacheEntity group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GroupChildCacheEntity that = (GroupChildCacheEntity) o;
        return Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), group);
    }
}
