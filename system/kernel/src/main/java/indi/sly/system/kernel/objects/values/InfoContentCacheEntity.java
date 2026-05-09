package indi.sly.system.kernel.objects.values;

import com.redis.om.spring.annotations.Document;
import indi.sly.system.kernel.core.values.ACacheEntity;
import org.springframework.data.annotation.Reference;

import java.util.Objects;

@Document("InfoContentObject")
public class InfoContentCacheEntity extends ACacheEntity {
    @Reference
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
