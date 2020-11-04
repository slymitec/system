package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.functions.*;
import indi.sly.system.kernel.objects.entities.InfoEntity;
import indi.sly.system.kernel.objects.prototypes.StatusDefinition;
import indi.sly.system.kernel.objects.types.TypeObject;
import indi.sly.system.kernel.processes.entities.ProcessEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProcessObjectProcessorRegister {
    public ProcessObjectProcessorRegister() {
        this.readProcessHandleTables = new ArrayList<>();
        this.writeProcessHandleTables = new ArrayList<>();
    }

    private Function<ProcessEntity, UUID> process;
    private final List<Function2<byte[], byte[], ProcessEntity>> readProcessHandleTables;
    private final List<Consumer2<ProcessEntity, byte[]>> writeProcessHandleTables;

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
}
