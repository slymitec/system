package indi.sly.system.kernel.memory.caches;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InfoObjectCacheDefinition {
    public InfoObjectCacheDefinition() {
        this.date = new ConcurrentHashMap<>();
    }

    private UUID id;
    private UUID type;
    private long space;
    private final Map<Long, Date> date;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getType() {
        return type;
    }

    public void setType(UUID type) {
        this.type = type;
    }

    public long getSpace() {
        return space;
    }

    public void setSpace(long space) {
        this.space = space;
    }

    public Map<Long, Date> getDate() {
        return date;
    }
}
