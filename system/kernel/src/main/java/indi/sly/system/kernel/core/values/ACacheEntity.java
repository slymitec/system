package indi.sly.system.kernel.core.values;

import com.github.f4b6a3.ulid.Ulid;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

import java.util.Objects;
import java.util.UUID;

public abstract class ACacheEntity extends AEntity {
    @Id
    private Ulid id;
    private UUID cacheRepositoryId;
    private long duration;
    @Version
    private long version;

    public Ulid getId() {
        return id;
    }

    public void setId(Ulid id) {
        this.id = id;
    }

    public UUID getCacheRepositoryId() {
        return cacheRepositoryId;
    }

    public void setCacheRepositoryId(UUID cacheRepositoryId) {
        this.cacheRepositoryId = cacheRepositoryId;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ACacheEntity that = (ACacheEntity) o;
        return duration == that.duration && Objects.equals(id, that.id) && Objects.equals(cacheRepositoryId, that.cacheRepositoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cacheRepositoryId, duration);
    }
}
