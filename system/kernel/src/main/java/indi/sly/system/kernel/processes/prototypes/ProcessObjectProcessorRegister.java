package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.functions.*;
import indi.sly.system.kernel.processes.entities.ProcessEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProcessObjectProcessorRegister {
    public ProcessObjectProcessorRegister() {
        this.readProcessHandleTables = new ArrayList<>();
        this.writeProcessHandleTables = new ArrayList<>();
        this.readProcessStatistics = new ArrayList<>();
        this.writeProcessStatistics = new ArrayList<>();
        this.readProcessTokens = new ArrayList<>();
        this.writeProcessTokens = new ArrayList<>();
    }

    private Function<ProcessEntity, UUID> process;
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
