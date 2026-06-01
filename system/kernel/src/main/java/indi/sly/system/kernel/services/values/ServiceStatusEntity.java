package indi.sly.system.kernel.services.values;

import indi.sly.system.common.supports.CollectionUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.values.APersistentEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Kernel_ServiceStatus")
public class ServiceStatusEntity extends APersistentEntity {
    public ServiceStatusEntity() {
        this.dependencies = new HashSet<>();
        this.dependents = new HashSet<>();
    }

    @Id
    @Column(columnDefinition = "uniqueidentifier", name = "Id", nullable = false, updatable = false)
    protected UUID id;

    @Column(columnDefinition = "uniqueidentifier", name = "ProcessId", nullable = false)
    protected UUID processId;

    @ManyToMany
    @JoinTable(name = "Kernel_ServiceStatus_Relations", joinColumns = @JoinColumn(name = "ParentId"), inverseJoinColumns = @JoinColumn(name = "ChildId"))
    private final Set<ServiceStatusEntity> dependencies;

    @ManyToMany(mappedBy = "dependencies")
    private final Set<ServiceStatusEntity> dependents;

    @Column(name = "Mode", nullable = false)
    protected Long mode;

    @Column(name = "Independence", nullable = false)
    private boolean independence;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProcessId() {
        return processId;
    }

    public void setProcessId(UUID processId) {
        this.processId = processId;
    }

    public void addDependency(ServiceStatusEntity dependency) {
        if (ObjectUtil.allNotNull(dependency) && !this.dependencies.contains(dependency)) {
            this.dependencies.add(dependency);
            dependency.dependents.add(this);
        }
    }

    public void removeDependency(ServiceStatusEntity dependency) {
        if (ObjectUtil.allNotNull(dependency) && this.dependencies.contains(dependency)) {
            this.dependencies.remove(dependency);
            if (dependency.dependents.contains(this)) {
                dependency.dependents.remove(this);
            }
        }
    }

    public Set<ServiceStatusEntity> getDependencies() {
        return CollectionUtil.unmodifiable(this.dependencies);
    }

    public Set<ServiceStatusEntity> getDependents() {
        return CollectionUtil.unmodifiable(this.dependents);
    }

    public long getMode() {
        return this.mode;
    }

    public void setMode(long mode) {
        this.mode = mode;
    }

    public boolean isIndependence() {
        return this.independence;
    }

    public void setIndependence(boolean independence) {
        this.independence = independence;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ServiceStatusEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
