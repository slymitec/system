package indi.sly.system.kernel.processes.values;

import indi.sly.system.common.values.PathDefinition;
import indi.sly.system.kernel.core.values.APersistentEntity;
import indi.sly.system.kernel.objects.values.InfoOpenDefinition;

import java.util.*;

public class ProcessInfoEntryEntity extends APersistentEntity {
    public ProcessInfoEntryEntity() {
        this.date = new HashMap<>();
    }

    private UUID index;
    private final Map<Long, Long> date;
    private UUID id;
    private PathDefinition path;
    private InfoOpenDefinition infoOpen;
    private boolean unsupportedDelete;

    public UUID getIndex() {
        return this.index;
    }

    public void setIndex(UUID index) {
        this.index = index;
    }

    public Map<Long, Long> getDate() {
        return this.date;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PathDefinition getPath() {
        return path;
    }

    public void setPath(PathDefinition path) {
        this.path = path;
    }

    public InfoOpenDefinition getInfoOpen() {
        return this.infoOpen;
    }

    public void setInfoOpen(InfoOpenDefinition infoOpen) {
        this.infoOpen = infoOpen;
    }

    public boolean isUnsupportedDelete() {
        return this.unsupportedDelete;
    }

    public void setUnsupportedDelete(boolean unsupportedDelete) {
        this.unsupportedDelete = unsupportedDelete;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProcessInfoEntryEntity that)) return false;
        return unsupportedDelete == that.unsupportedDelete && Objects.equals(index, that.index) && Objects.equals(date, that.date) && Objects.equals(id, that.id) && Objects.equals(path, that.path) && Objects.equals(infoOpen, that.infoOpen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, date, id, path, infoOpen, unsupportedDelete);
    }
}
