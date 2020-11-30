package indi.sly.system.kernel.memory.caches.definition;

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
    private final Map<Long, Long> date;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getType() {
        return this.type;
    }

    public void setType(UUID type) {
        this.type = type;
    }

    public long getSpace() {
        return this.space;
    }

    public void setSpace(long space) {
        this.space = space;
    }

    public Map<Long, Long> getDate() {
        return this.date;
    }
}
