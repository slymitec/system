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
        if (!(o instanceof GroupChildCacheEntity cache)) return false;
        return Objects.equals(id, cache.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
