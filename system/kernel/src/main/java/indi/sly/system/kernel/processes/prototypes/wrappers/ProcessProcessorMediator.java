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

    private ProcessProcessorSelfFunction self;
    private final Set<ProcessProcessorReadProcessStatusFunction> readProcessStatuses;
    private final Set<ProcessProcessorWriteProcessStatusConsumer> writeProcessStatuses;
    private final Set<ProcessProcessorReadProcessComponentFunction> readProcessCommunications;
    private final Set<ProcessProcessorWriteProcessComponentConsumer> writeProcessCommunications;
    private final Set<ProcessProcessorReadProcessComponentFunction> readProcessContexts;
    private final Set<ProcessProcessorWriteProcessComponentConsumer> writeProcessContexts;
    private final Set<ProcessProcessorReadProcessComponentFunction> readProcessHandleTables;
    private final Set<ProcessProcessorWriteProcessComponentConsumer> writeProcessHandleTables;
    private final Set<ProcessProcessorReadProcessComponentFunction> readProcessStatistics;
    private final Set<ProcessProcessorWriteProcessComponentConsumer> writeProcessStatistics;
    private final Set<ProcessProcessorReadProcessComponentFunction> readProcessTokens;
    private final Set<ProcessProcessorWriteProcessComponentConsumer> writeProcessTokens;

    public ProcessProcessorSelfFunction getSelf() {
        return this.self;
    }

    public void setSelf(ProcessProcessorSelfFunction self) {
        this.self = self;
    }

    public Set<ProcessProcessorReadProcessStatusFunction> getReadProcessStatuses() {
        return this.readProcessStatuses;
    }

    public Set<ProcessProcessorWriteProcessStatusConsumer> getWriteProcessStatuses() {
        return this.writeProcessStatuses;
    }

    public Set<ProcessProcessorReadProcessComponentFunction> getReadProcessCommunications() {
        return this.readProcessCommunications;
    }

    public Set<ProcessProcessorWriteProcessComponentConsumer> getWriteProcessCommunications() {
        return this.writeProcessCommunications;
    }

    public Set<ProcessProcessorReadProcessComponentFunction> getReadProcessContexts() {
        return this.readProcessContexts;
    }

    public Set<ProcessProcessorWriteProcessComponentConsumer> getWriteProcessContexts() {
        return this.writeProcessContexts;
    }

    public Set<ProcessProcessorReadProcessComponentFunction> getReadProcessHandleTables() {
        return this.readProcessHandleTables;
    }

    public Set<ProcessProcessorWriteProcessComponentConsumer> getWriteProcessHandleTables() {
        return this.writeProcessHandleTables;
    }

    public Set<ProcessProcessorReadProcessComponentFunction> getReadProcessStatistics() {
        return this.readProcessStatistics;
    }

    public Set<ProcessProcessorWriteProcessComponentConsumer> getWriteProcessStatistics() {
        return this.writeProcessStatistics;
    }

    public Set<ProcessProcessorReadProcessComponentFunction> getReadProcessTokens() {
        return this.readProcessTokens;
    }

    public Set<ProcessProcessorWriteProcessComponentConsumer> getWriteProcessTokens() {
        return this.writeProcessTokens;
    }
}
