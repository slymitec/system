package indi.sly.system.kernel.processes.values;

import indi.sly.system.kernel.core.values.APersistentEntity;
import indi.sly.system.kernel.memory.repositories.prototypes.BinarySerializationAttributeConverterComponent;
import jakarta.persistence.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "Kernel_Processes")
public class ProcessEntity extends APersistentEntity {
    @Id
    @Column(columnDefinition = "uniqueidentifier", name = "Id", nullable = false, updatable = false)
    protected UUID id;
    @Column(name = "Status", nullable = false)
    protected long status;
    @Column(columnDefinition = "uniqueidentifier", name = "Parent_ProcessId", nullable = true)
    protected UUID parentProcessID;
    @Basic(fetch = FetchType.LAZY)
    @Column(length = 4096, name = "Communication", nullable = false)
    @Convert(converter = BinarySerializationAttributeConverterComponent.class)
    @Lob
    protected ProcessCommunicationEntity communication;
    @Basic(fetch = FetchType.LAZY)
    @Column(length = 4096, name = "Context", nullable = false)
    @Convert(converter = BinarySerializationAttributeConverterComponent.class)
    @Lob
    protected ProcessContextEntity context;
    @Column(length = 4096, name = "Info_Table", nullable = false)
    @Convert(converter = BinarySerializationAttributeConverterComponent.class)
    @Lob
    protected ProcessInfoTableEntity infoTable;
    @Column(length = 4096, name = "Session_Info", nullable = false)
    @Convert(converter = BinarySerializationAttributeConverterComponent.class)
    @Lob
    protected ProcessSessionEntity session;
    @Basic(fetch = FetchType.LAZY)
    @Column(length = 4096, name = "Statistics_Info", nullable = false)
    @Convert(converter = BinarySerializationAttributeConverterComponent.class)
    @Lob
    protected ProcessStatisticsEntity statistics;
    @Column(length = 4096, name = "Token", nullable = false)
    @Convert(converter = BinarySerializationAttributeConverterComponent.class)
    @Lob
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
