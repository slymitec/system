package indi.sly.system.kernel.objects.values;

import indi.sly.system.kernel.core.values.ACacheEntity;
import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RObjectField;

import java.util.Objects;

@REntity
public class InfoContentCacheEntity extends ACacheEntity {
    @RObjectField
    private InfoCacheEntity info;

    public InfoCacheEntity getInfo() {
        return info;
    }

    public void setInfo(InfoCacheEntity info) {
        this.info = info;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InfoContentCacheEntity that = (InfoContentCacheEntity) o;
        return Objects.equals(info, that.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), info);
    }
}
