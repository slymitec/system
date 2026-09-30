package indi.sly.system.kernel.processes.values;

import indi.sly.system.kernel.core.values.APersistentEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "Kernel_Processes")
public class ProcessEntity extends APersistentEntity {
    @Id
    @Column(name = "Id", nullable = false, updatable = false)
    protected UUID id;
    @Column(name = "Status", nullable = false)
    protected long status;
    @Column(name = "Parent_ProcessId", nullable = true)
    protected UUID parentProcessID;
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "json", name = "Communication", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    protected ProcessCommunicationEntity communication;
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "json", name = "Context", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    protected ProcessContextEntity context;
    @Column(columnDefinition = "json", name = "Info_Table", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    protected ProcessInfoTableEntity infoTable;
    @Column(columnDefinition = "json", name = "Session_Info", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    protected ProcessSessionEntity session;
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "json", name = "Statistics_Info", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    protected ProcessStatisticsEntity statistics;
    @Column(columnDefinition = "json", name = "Token", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    protected ProcessTokenEntity token;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public long getStatus() {
        return this.status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public UUID getParentProcessID() {
        return this.parentProcessID;
    }

    public void setParentProcessID(UUID parentProcessID) {
        this.parentProcessID = parentProcessID;
    }

    public ProcessCommunicationEntity getCommunication() {
        return this.communication;
    }

    public void setCommunication(ProcessCommunicationEntity communication) {
        this.communication = communication;
    }

    public ProcessContextEntity getContext() {
        return this.context;
    }

    public void setContext(ProcessContextEntity context) {
        this.context = context;
    }

    public ProcessInfoTableEntity getInfoTable() {
        return this.infoTable;
    }

    public void setInfoTable(ProcessInfoTableEntity infoTable) {
        this.infoTable = infoTable;
    }

    public ProcessSessionEntity getSession() {
        return this.session;
    }

    public void setSession(ProcessSessionEntity session) {
        this.session = session;
    }

    public ProcessStatisticsEntity getStatistics() {
        return this.statistics;
    }

    public void setStatistics(ProcessStatisticsEntity statistics) {
        this.statistics = statistics;
    }

    public ProcessTokenEntity getToken() {
        return this.token;
    }

    public void setToken(ProcessTokenEntity token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProcessEntity process)) return false;
        return Objects.equals(id, process.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
