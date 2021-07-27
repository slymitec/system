package indi.sly.system.kernel.processes.prototypes.wrappers;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.lang.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashSet;
import java.util.Set;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessProcessorMediator extends APrototype {
    public ProcessProcessorMediator() {
        this.readProcessStatuses = new HashSet<>();
        this.writeProcessStatuses = new HashSet<>();
        this.readProcessCommunications = new HashSet<>();
        this.writeProcessCommunications = new HashSet<>();
        this.readProcessContexts = new HashSet<>();
        this.writeProcessContexts = new HashSet<>();
        this.readProcessHandleTables = new HashSet<>();
        this.writeProcessHandleTables = new HashSet<>();
        this.readProcessStatistics = new HashSet<>();
        this.writeProcessStatistics = new HashSet<>();
        this.readProcessTokens = new HashSet<>();
        this.writeProcessTokens = new HashSet<>();
    }

    private ProcessSelfFunction process;
    private final Set<ReadProcessStatusFunction> readProcessStatuses;
    private final Set<WriteProcessStatusConsumer> writeProcessStatuses;
    private final Set<ReadProcessComponentFunction> readProcessCommunications;
    private final Set<WriteProcessComponentConsumer> writeProcessCommunications;
    private final Set<ReadProcessComponentFunction> readProcessContexts;
    private final Set<WriteProcessComponentConsumer> writeProcessContexts;
    private final Set<ReadProcessComponentFunction> readProcessHandleTables;
    private final Set<WriteProcessComponentConsumer> writeProcessHandleTables;
    private final Set<ReadProcessComponentFunction> readProcessStatistics;
    private final Set<WriteProcessComponentConsumer> writeProcessStatistics;
    private final Set<ReadProcessComponentFunction> readProcessTokens;
    private final Set<WriteProcessComponentConsumer> writeProcessTokens;

    public ProcessSelfFunction getProcess() {
        return this.process;
    }

    public void setProcess(ProcessSelfFunction process) {
        this.process = process;
    }

    public Set<ReadProcessStatusFunction> getReadProcessStatuses() {
        return this.readProcessStatuses;
    }

    public Set<WriteProcessStatusConsumer> getWriteProcessStatuses() {
        return this.writeProcessStatuses;
    }

    public Set<ReadProcessComponentFunction> getReadProcessCommunications() {
        return this.readProcessCommunications;
    }

    public Set<WriteProcessComponentConsumer> getWriteProcessCommunications() {
        return this.writeProcessCommunications;
    }

    public Set<ReadProcessComponentFunction> getReadProcessContexts() {
        return this.readProcessContexts;
    }

    public Set<WriteProcessComponentConsumer> getWriteProcessContexts() {
        return this.writeProcessContexts;
    }

    public Set<ReadProcessComponentFunction> getReadProcessHandleTables() {
        return this.readProcessHandleTables;
    }

    public Set<WriteProcessComponentConsumer> getWriteProcessHandleTables() {
        return this.writeProcessHandleTables;
    }

    public Set<ReadProcessComponentFunction> getReadProcessStatistics() {
        return this.readProcessStatistics;
    }

    public Set<WriteProcessComponentConsumer> getWriteProcessStatistics() {
        return this.writeProcessStatistics;
    }

    public Set<ReadProcessComponentFunction> getReadProcessTokens() {
        return this.readProcessTokens;
    }

    public Set<WriteProcessComponentConsumer> getWriteProcessTokens() {
        return this.writeProcessTokens;
    }
}
