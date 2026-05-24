package indi.sly.system.kernel.objects.values;

import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.values.ACacheEntity;
import org.redisson.api.annotation.REntity;


import java.util.Objects;
import java.util.UUID;

@REntity
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
        if (!(o instanceof InfoCacheEntity cache)) return false;
        return Objects.equals(id, cache.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
