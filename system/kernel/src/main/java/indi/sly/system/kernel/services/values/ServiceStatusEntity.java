package indi.sly.system.kernel.services.values;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @JoinTable(name = "Kernel_ServiceStatus_Relations", joinColumns = @JoinColumn(name = "parentId"), inverseJoinColumns = @JoinColumn(name = "childId")
    )
    @JsonIgnoreProperties("dependents")
    private Set<ServiceStatusEntity> dependencies;

    @ManyToMany(mappedBy = "dependencies")
    @JsonIgnoreProperties("dependencies")
    private Set<ServiceStatusEntity> dependents;

    @Column(name = "Independence", nullable = false)
    private boolean independence;

    public UUID getId() {
        return id;
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
        if (ObjectUtil.allNotNull(dependency)) {
            this.dependencies.add(dependency);
            dependency.getDependents().add(this);
        }
    }

    public void removeDependency(ServiceStatusEntity dependency) {
        if (ObjectUtil.allNotNull(dependency)) {
            this.dependencies.remove(dependency);
            dependency.getDependents().remove(this);
        }
    }

    public Set<ServiceStatusEntity> getDependencies() {
        return this.dependencies;
    }

    public Set<ServiceStatusEntity> getDependents() {
        return this.dependents;
    }

    public boolean isIndependence() {
        return this.independence;
    }

    public void setIndependence(boolean independence) {
        this.independence = independence;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ServiceStatusEntity that = (ServiceStatusEntity) o;
        return independence == that.independence && Objects.equals(id, that.id) && Objects.equals(processId, that.processId) && Objects.equals(dependencies, that.dependencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, processId, dependencies, independence);
    }
}
