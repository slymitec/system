package indi.sly.system.services.jobs.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.prototypes.ADefinitionObject;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import indi.sly.system.services.jobs.lang.TaskProcessorContentFunction;
import indi.sly.system.services.jobs.lang.TaskProcessorEndConsumer;
import indi.sly.system.services.jobs.lang.TaskProcessorRunConsumer;
import indi.sly.system.services.jobs.lang.TaskProcessorStartConsumer;
import indi.sly.system.services.jobs.prototypes.mediators.TaskProcessorMediator;
import indi.sly.system.services.jobs.values.TaskDefinition;
import indi.sly.system.services.jobs.values.TaskStatusDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskObject extends ADefinitionObject<TaskDefinition> {
    protected TaskProcessorMediator processorMediator;
    protected TaskStatusDefinition status;

    public UUID getId() {
        return this.definition.getId();
    }

    public String getName() {
        return this.definition.getName();
    }

    public long getAttribute() {
        return this.definition.getAttribute();
    }

    public long getRuntime() {
        return this.status.getRuntime();
    }

    public void start() {
        List<TaskProcessorStartConsumer> resolvers = this.processorMediator.getStarts();

        for (TaskProcessorStartConsumer resolver : resolvers) {
            resolver.accept(this.definition, this.status);
        }
    }

    public void end() {
        List<TaskProcessorEndConsumer> resolvers = this.processorMediator.getEnds();

        for (TaskProcessorEndConsumer resolver : resolvers) {
            resolver.accept(this.definition, this.status);
        }
    }

    public synchronized TaskContentObject getContent() {
        ThreadContextObject threadContext = null;

        List<TaskProcessorContentFunction> resolvers = this.processorMediator.getContents();

        for (TaskProcessorContentFunction resolver : resolvers) {
            threadContext = resolver.apply(this.definition, this.status, threadContext);
        }

        TaskContentObject taskContent = this.coreManager.create(TaskContentObject.class);
        taskContent.setDefinition(this.definition);
        taskContent.setBase(this);
        taskContent.processorMediator = this.processorMediator;
        taskContent.status = status;
        taskContent.threadContext = threadContext;

        return taskContent;
    }
}
