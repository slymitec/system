package indi.sly.system.kernel.processes.prototypes.wrappers;

import indi.sly.system.kernel.core.prototypes.wrappers.AMediator;
import indi.sly.system.kernel.processes.lang.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.HashSet;
import java.util.Set;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessProcessorMediator extends AMediator {
    public ProcessProcessorMediator() {
        this.readProcessStatuses = new HashSet<>();
        this.writeProcessStatuses = new HashSet<>();
        this.readProcessCommunications = new HashSet<>();
        this.writeProcessCommunications = new HashSet<>();
        this.readProcessContexts = new HashSet<>();
        this.writeProcessContexts = new HashSet<>();
        this.readProcessInfoTables = new HashSet<>();
        this.writeProcessInfoTables = new HashSet<>();
        this.readProcessSessions = new HashSet<>();
        this.writeProcessSessions = new HashSet<>();
        this.readProcessStatistics = new HashSet<>();
        this.writeProcessStatistics = new HashSet<>();
        this.readProcessTokens = new HashSet<>();
        this.writeProcessTokens = new HashSet<>();
    }

    private ProcessProcessorSelfFunction self;
    private final Set<ProcessProcessorReadStatusFunction> readProcessStatuses;
    private final Set<ProcessProcessorWriteStatusConsumer> writeProcessStatuses;
    private final Set<ProcessProcessorReadComponentFunction> readProcessCommunications;
    private final Set<ProcessProcessorWriteComponentConsumer> writeProcessCommunications;
    private final Set<ProcessProcessorReadComponentFunction> readProcessContexts;
    private final Set<ProcessProcessorWriteComponentConsumer> writeProcessContexts;
    private final Set<ProcessProcessorReadComponentFunction> readProcessInfoTables;
    private final Set<ProcessProcessorWriteComponentConsumer> writeProcessInfoTables;
    private final Set<ProcessProcessorReadComponentFunction> readProcessSessions;
    private final Set<ProcessProcessorWriteComponentConsumer> writeProcessSessions;
    private final Set<ProcessProcessorReadComponentFunction> readProcessStatistics;
    private final Set<ProcessProcessorWriteComponentConsumer> writeProcessStatistics;
    private final Set<ProcessProcessorReadComponentFunction> readProcessTokens;
    private final Set<ProcessProcessorWriteComponentConsumer> writeProcessTokens;

    public ProcessProcessorSelfFunction getSelf() {
        return this.self;
    }

    public void setSelf(ProcessProcessorSelfFunction self) {
        this.self = self;
    }

    public Set<ProcessProcessorReadStatusFunction> getReadProcessStatuses() {
        return this.readProcessStatuses;
    }

    public Set<ProcessProcessorWriteStatusConsumer> getWriteProcessStatuses() {
        return this.writeProcessStatuses;
    }

    public Set<ProcessProcessorReadComponentFunction> getReadProcessCommunications() {
        return this.readProcessCommunications;
    }

    public Set<ProcessProcessorWriteComponentConsumer> getWriteProcessCommunications() {
        return this.writeProcessCommunications;
    }

    public Set<ProcessProcessorReadComponentFunction> getReadProcessContexts() {
        return this.readProcessContexts;
    }

    public Set<ProcessProcessorWriteComponentConsumer> getWriteProcessContexts() {
        return this.writeProcessContexts;
    }

    public Set<ProcessProcessorReadComponentFunction> getReadProcessInfoTables() {
        return this.readProcessInfoTables;
    }

    public Set<ProcessProcessorWriteComponentConsumer> getWriteProcessInfoTables() {
        return this.writeProcessInfoTables;
    }

    public Set<ProcessProcessorReadComponentFunction> getReadProcessSessions() {
        return this.readProcessSessions;
    }

    public Set<ProcessProcessorWriteComponentConsumer> getWriteProcessSessions() {
        return this.writeProcessSessions;
    }

    public Set<ProcessProcessorReadComponentFunction> getReadProcessStatistics() {
        return this.readProcessStatistics;
    }

    public Set<ProcessProcessorWriteComponentConsumer> getWriteProcessStatistics() {
        return this.writeProcessStatistics;
    }

    public Set<ProcessProcessorReadComponentFunction> getReadProcessTokens() {
        return this.readProcessTokens;
    }

    public Set<ProcessProcessorWriteComponentConsumer> getWriteProcessTokens() {
        return this.writeProcessTokens;
    }
}
