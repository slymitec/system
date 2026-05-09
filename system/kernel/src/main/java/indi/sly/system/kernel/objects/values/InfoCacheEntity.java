package indi.sly.system.kernel.objects.values;

import com.redis.om.spring.annotations.Document;
import indi.sly.system.common.values.IdentifierDefinition;
import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.values.ACacheEntity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Document("InfoObject")
public class InfoCacheEntity extends ACacheEntity {
    private UUID infoId;
    private UUID poolId;
    private PathDefinition path;

    public UUID getInfoId() {
        return this.infoId;
    }

    public void setInfoId(UUID infoId) {
        this.infoId = infoId;
    }

    public UUID getPoolId() {
        return poolId;
    }

    public void setPoolId(UUID poolId) {
        this.poolId = poolId;
    }

    public PathDefinition getPath() {
        return this.path;
    }

    public void setPath(PathDefinition path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InfoCacheEntity cache = (InfoCacheEntity) o;
        return Objects.equals(infoId, cache.infoId) && Objects.equals(poolId, cache.poolId) && Objects.equals(path, cache.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), infoId, poolId, path);
    }
}
