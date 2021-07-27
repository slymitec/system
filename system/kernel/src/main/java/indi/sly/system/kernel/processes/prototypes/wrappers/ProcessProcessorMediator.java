package indi.sly.system.kernel.processes.prototypes.wrappers;

import indi.sly.system.common.lang.Function1;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.lang.*;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessProcessorMediator extends APrototype {
    public ProcessProcessorMediator() {
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

    private ProcessSelfFunction process;
    private final List<ReadProcessStatusFunction> readProcessStatuses;
    private final List<WriteProcessStatusConsumer> writeProcessStatuses;
    private final List<ReadProcessComponentFunction> readProcessCommunications;
    private final List<WriteProcessComponentConsumer> writeProcessCommunications;
    private final List<ReadProcessComponentFunction> readProcessContexts;
    private final List<WriteProcessComponentConsumer> writeProcessContexts;
    private final List<ReadProcessComponentFunction> readProcessHandleTables;
    private final List<WriteProcessComponentConsumer> writeProcessHandleTables;
    private final List<ReadProcessComponentFunction> readProcessStatistics;
    private final List<WriteProcessComponentConsumer> writeProcessStatistics;
    private final List<ReadProcessComponentFunction> readProcessTokens;
    private final List<WriteProcessComponentConsumer> writeProcessTokens;

    public ProcessSelfFunction getProcess() {
        return this.process;
    }

    public void setProcess(ProcessSelfFunction process) {
        this.process = process;
    }

    public List<ReadProcessStatusFunction> getReadProcessStatuses() {
        return this.readProcessStatuses;
    }

    public List<WriteProcessStatusConsumer> getWriteProcessStatuses() {
        return this.writeProcessStatuses;
    }

    public List<ReadProcessComponentFunction> getReadProcessCommunications() {
        return this.readProcessCommunications;
    }

    public List<WriteProcessComponentConsumer> getWriteProcessCommunications() {
        return this.writeProcessCommunications;
    }

    public List<ReadProcessComponentFunction> getReadProcessContexts() {
        return this.readProcessContexts;
    }

    public List<WriteProcessComponentConsumer> getWriteProcessContexts() {
        return this.writeProcessContexts;
    }

    public List<ReadProcessComponentFunction> getReadProcessHandleTables() {
        return this.readProcessHandleTables;
    }

    public List<WriteProcessComponentConsumer> getWriteProcessHandleTables() {
        return this.writeProcessHandleTables;
    }

    public List<ReadProcessComponentFunction> getReadProcessStatistics() {
        return this.readProcessStatistics;
    }

    public List<WriteProcessComponentConsumer> getWriteProcessStatistics() {
        return this.writeProcessStatistics;
    }

    public List<ReadProcessComponentFunction> getReadProcessTokens() {
        return this.readProcessTokens;
    }

    public List<WriteProcessComponentConsumer> getWriteProcessTokens() {
        return this.writeProcessTokens;
    }
}
