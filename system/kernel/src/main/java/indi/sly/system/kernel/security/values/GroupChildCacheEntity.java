package indi.sly.system.kernel.security.values;

import com.redis.om.spring.annotations.Document;
import indi.sly.system.kernel.core.values.ACacheEntity;
import org.springframework.data.annotation.Reference;

import java.util.Objects;

@Document("GroupChildObject")
public class GroupChildCacheEntity extends ACacheEntity {
    @Reference
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
