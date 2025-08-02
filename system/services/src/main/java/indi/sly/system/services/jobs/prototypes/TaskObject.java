package indi.sly.system.services.jobs.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AIndependentValueProcessObject;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import indi.sly.system.services.jobs.lang.TaskProcessorContentFunction;
import indi.sly.system.services.jobs.lang.TaskProcessorFinishConsumer;
import indi.sly.system.services.jobs.lang.TaskProcessorRunConsumer;
import indi.sly.system.services.jobs.lang.TaskProcessorStartConsumer;
import indi.sly.system.services.jobs.prototypes.wrappers.TaskProcessorMediator;
import indi.sly.system.services.jobs.values.TaskDefinition;
import indi.sly.system.services.jobs.values.TaskStatusDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskObject extends AIndependentValueProcessObject<TaskDefinition> {
    protected TaskProcessorMediator processorMediator;
    protected TaskStatusDefinition status;

    public UUID getID() {
        try {
            this.lock(LockType.READ);
            this.init();

            return this.value.getID();
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public long getRuntime() {
        return this.status.getRuntime();
    }

    public void start() {
        List<TaskProcessorStartConsumer> resolvers = this.processorMediator.getStarts();

        try {
            this.lock(LockType.READ);
            this.init();

            for (TaskProcessorStartConsumer resolver : resolvers) {
                resolver.accept(this.value, this.status);
            }
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public void finish() {
        List<TaskProcessorFinishConsumer> resolvers = this.processorMediator.getFinishes();

        try {
            this.lock(LockType.READ);
            this.init();

            for (TaskProcessorFinishConsumer resolver : resolvers) {
                resolver.accept(this.value, this.status);
            }
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public synchronized void run(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        TaskContentObject content = this.getContent();

        List<TaskProcessorRunConsumer> resolvers = this.processorMediator.getRuns();

        try {
            this.lock(LockType.READ);
            this.init();

            for (TaskProcessorRunConsumer resolver : resolvers) {
                resolver.accept(this.value, this.status, name, this::run, content);
            }
        } finally {
            this.lock(LockType.NONE);
        }
    }

    public synchronized TaskContentObject getContent() {
        ThreadContextObject threadContext = null;

        List<TaskProcessorContentFunction> resolvers = this.processorMediator.getContents();

        try {
            this.lock(LockType.READ);
            this.init();

            for (TaskProcessorContentFunction resolver : resolvers) {
                threadContext = resolver.apply(this.value, this.status, threadContext);
            }
        } finally {
            this.lock(LockType.NONE);
        }

        TaskContentObject taskContent = this.factoryManager.create(TaskContentObject.class);
        taskContent.threadContext = threadContext;

        return taskContent;
    }
}
