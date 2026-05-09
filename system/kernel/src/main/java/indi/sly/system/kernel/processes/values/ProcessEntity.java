package indi.sly.system.kernel.processes.values;

import indi.sly.system.kernel.core.values.APersistentEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "Kernel_Processes")
public class ProcessEntity extends APersistentEntity {
    @Id
    @Column(columnDefinition = "uniqueidentifier", name = "ID", nullable = false, updatable = false)
    protected UUID id;
    @Column(name = "Status", nullable = false)
    protected long status;
    @Column(columnDefinition = "uniqueidentifier", name = "Parent_ProcessID", nullable = true)
    protected UUID parentProcessID;
    @Column(length = 4096, name = "Communication", nullable = false)
    protected byte[] communication;
    @Column(length = 4096, name = "Context", nullable = false)
    protected byte[] context;
    @Column(length = 4096, name = "Info_Table", nullable = false)
    protected byte[] infoTable;
    @Column(length = 4096, name = "Session_Info", nullable = false)
    protected byte[] session;
    @Column(length = 4096, name = "Statistics_Info", nullable = false)
    protected byte[] statistics;
    @Column(length = 4096, name = "Token", nullable = false)
    protected byte[] token;

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

    public byte[] getCommunication() {
        return this.communication;
    }

    public void setCommunication(byte[] communication) {
        this.communication = communication;
    }

    public byte[] getContext() {
        return this.context;
    }

    public void setContext(byte[] context) {
        this.context = context;
    }

    public byte[] getInfoTable() {
        return this.infoTable;
    }

    public void setInfoTable(byte[] infoTable) {
        this.infoTable = infoTable;
    }

    public byte[] getSession() {
        return this.session;
    }

    public void setSession(byte[] session) {
        this.session = session;
    }

    public byte[] getStatistics() {
        return this.statistics;
    }

    public void setStatistics(byte[] statistics) {
        this.statistics = statistics;
    }

    public byte[] getToken() {
        return this.token;
    }

    public void setToken(byte[] token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProcessEntity that = (ProcessEntity) o;
        return status == that.status && Objects.equals(id, that.id) && Objects.equals(parentProcessID, that.parentProcessID) && Objects.deepEquals(communication, that.communication) && Objects.deepEquals(context, that.context) && Objects.deepEquals(infoTable, that.infoTable) && Objects.deepEquals(session, that.session) && Objects.deepEquals(statistics, that.statistics) && Objects.deepEquals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, parentProcessID, Arrays.hashCode(communication), Arrays.hashCode(context), Arrays.hashCode(infoTable), Arrays.hashCode(session), Arrays.hashCode(statistics), Arrays.hashCode(token));
    }
}
