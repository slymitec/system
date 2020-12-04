package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.functions.*;
import indi.sly.system.kernel.processes.values.ProcessEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProcessObjectProcessorRegister {
    public ProcessObjectProcessorRegister() {
        this.readProcessStatuses = new ArrayList<>();
        this.writeProcessStatuses = new ArrayList<>();
        this.readProcessCommunications = new ArrayList<>();
        this.writeProcessCommunications = new ArrayList<>();
        this.readProcessContexts = new ArrayList<>();
        this.writeProcessContexts = new ArrayList<>();
        this.readProcessHandleTables = new ArrayList<>();
        this.writeProcessHandleTables = new ArrayList<>();
        this.readProcessStatistics = new ArrayList<>();
        this.writeProcessStatistics = new ArrayList<>();
        this.readProcessTokens = new ArrayList<>();
        this.writeProcessTokens = new ArrayList<>();
    }

    private Function<ProcessEntity, UUID> process;
    private final List<Function2<Long, Long, ProcessEntity>> readProcessStatuses;
    private final List<Consumer2<ProcessEntity, Long>> writeProcessStatuses;
    private final List<Function2<byte[], byte[], ProcessEntity>> readProcessCommunications;
    private final List<Consumer2<ProcessEntity, byte[]>> writeProcessCommunications;
    private final List<Function2<byte[], byte[], ProcessEntity>> readProcessContexts;
    private final List<Consumer2<ProcessEntity, byte[]>> writeProcessContexts;
    private final List<Function2<byte[], byte[], ProcessEntity>> readProcessHandleTables;
    private final List<Consumer2<ProcessEntity, byte[]>> writeProcessHandleTables;
    private final List<Function2<byte[], byte[], ProcessEntity>> readProcessStatistics;
    private final List<Consumer2<ProcessEntity, byte[]>> writeProcessStatistics;
    private final List<Function2<byte[], byte[], ProcessEntity>> readProcessTokens;
    private final List<Consumer2<ProcessEntity, byte[]>> writeProcessTokens;

    public Function<ProcessEntity, UUID> getProcess() {
        return this.process;
    }

    public void setProcess(Function<ProcessEntity, UUID> process) {
        this.process = process;
    }

    public List<Function2<Long, Long, ProcessEntity>> getReadProcessStatuses() {
        return this.readProcessStatuses;
    }

    public List<Consumer2<ProcessEntity, Long>> getWriteProcessStatuses() {
        return this.writeProcessStatuses;
    }

    public List<Function2<byte[], byte[], ProcessEntity>> getReadProcessCommunications() {
        return this.readProcessCommunications;
    }

    public List<Consumer2<ProcessEntity, byte[]>> getWriteProcessCommunications() {
        return this.writeProcessCommunications;
    }

    public List<Function2<byte[], byte[], ProcessEntity>> getReadProcessContexts() {
        return this.readProcessContexts;
    }

    public List<Consumer2<ProcessEntity, byte[]>> getWriteProcessContexts() {
        return this.writeProcessContexts;
    }

    public List<Function2<byte[], byte[], ProcessEntity>> getReadProcessHandleTables() {
        return this.readProcessHandleTables;
    }

    public List<Consumer2<ProcessEntity, byte[]>> getWriteProcessHandleTables() {
        return this.writeProcessHandleTables;
    }

    public List<Function2<byte[], byte[], ProcessEntity>> getReadProcessStatistics() {
        return this.readProcessStatistics;
    }

    public List<Consumer2<ProcessEntity, byte[]>> getWriteProcessStatistics() {
        return this.writeProcessStatistics;
    }

    public List<Function2<byte[], byte[], ProcessEntity>> getReadProcessTokens() {
        return this.readProcessTokens;
    }

    public List<Consumer2<ProcessEntity, byte[]>> getWriteProcessTokens() {
        return this.writeProcessTokens;
    }
}
