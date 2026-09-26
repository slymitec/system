package indi.sly.system.services.jobs.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.services.jobs.prototypes.processors.*;
import indi.sly.system.services.jobs.prototypes.mediators.TaskProcessorMediator;
import indi.sly.system.services.jobs.prototypes.mediators.CallContextProcessorMediator;
import indi.sly.system.services.jobs.values.TaskDefinition;
import indi.sly.system.services.jobs.values.TaskStatusDefinition;
import indi.sly.system.services.jobs.values.CallContextDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobFactory extends AFactory {
    public JobFactory() {
        this.taskResolvers = new CopyOnWriteArrayList<>();

        this.userContextCreateResolvers = new CopyOnWriteArrayList<>();
        this.userContextFinishResolvers = new CopyOnWriteArrayList<>();
    }

    protected final List<ITaskResolver> taskResolvers;
    protected final List<ICallContextCreateResolver> userContextCreateResolvers;
    protected final List<ICallContextEndResolver> userContextFinishResolvers;

    @Override
    public void init() {
        this.taskResolvers.add(this.coreManager.create(TaskCacheableObjectResolver.class));
        this.taskResolvers.add(this.coreManager.create(TaskCheckConditionResolver.class));
        this.taskResolvers.add(this.coreManager.create(TaskContentResolver.class));
        this.taskResolvers.add(this.coreManager.create(TaskDateResolver.class));
        this.taskResolvers.add(this.coreManager.create(TaskInitializerResolver.class));
        this.taskResolvers.add(this.coreManager.create(TaskProcessAndThreadResolver.class));
        this.taskResolvers.add(this.coreManager.create(TaskStatusRuntimeResolver.class));

        this.userContextCreateResolvers.add(this.coreManager.create(CallContextCreateThreadResolver.class));
        this.userContextCreateResolvers.add(this.coreManager.create(CallContextCreateCheckClientProcessIdResolver.class));
        this.userContextCreateResolvers.add(this.coreManager.create(CallContextCreateCheckProcessResolver.class));

        this.userContextFinishResolvers.add(this.coreManager.create(CallContextEndThreadResolver.class));

        Collections.sort(this.taskResolvers);
        Collections.sort(this.userContextCreateResolvers);
        Collections.sort(this.userContextFinishResolvers);
    }

    private TaskObject createTask(TaskProcessorMediator processorMediator, TaskDefinition definition, UUID handle) {
        TaskObject task = this.coreManager.create(TaskObject.class);

        task.setDefinition(definition);
        task.processorMediator = processorMediator;
        task.status = new TaskStatusDefinition();
        if (!ValueUtil.isAnyNullOrEmpty(handle)) {
            task.status.setHandle(handle);
        }

        return task;
    }

    public TaskObject buildTask(TaskDefinition task, UUID handle) {
        if (ObjectUtil.isAnyNull(task)) {
            throw new ConditionParametersException();
        }

        TaskProcessorMediator processorMediator = this.coreManager.create(TaskProcessorMediator.class);
        for (ITaskResolver resolver : this.taskResolvers) {
            resolver.resolve(task, processorMediator);
        }

        return this.createTask(processorMediator, task, handle);
    }

    public TaskBuilder createTask() {
        TaskBuilder taskBuilder = this.coreManager.create(TaskBuilder.class);

        taskBuilder.factory = this;

        return taskBuilder;
    }

    private CallContextObject createUserContext(CallContextDefinition definition) {
        CallContextObject userContext = this.coreManager.create(CallContextObject.class);

        userContext.setDefinition(definition);

        return userContext;
    }

    public CallContextObject buildUserContext(CallContextDefinition userContext) {
        if (ObjectUtil.isAnyNull(userContext)) {
            throw new ConditionParametersException();
        }

        return this.createUserContext(userContext);
    }

    public CallContextCreateBuilder createUserContextCreator() {
        CallContextProcessorMediator processorMediator = this.coreManager.create(CallContextProcessorMediator.class);

        for (ICallContextCreateResolver userContextCreateResolver : this.userContextCreateResolvers) {
            userContextCreateResolver.resolve(processorMediator);
        }

        CallContextCreateBuilder userContextCreateBuilder = this.coreManager.create(CallContextCreateBuilder.class);

        userContextCreateBuilder.processorMediator = processorMediator;
        userContextCreateBuilder.factory = this;

        return userContextCreateBuilder;
    }

    public CallContextFinishBuilder createUserContextFinish() {
        CallContextProcessorMediator processorMediator = this.coreManager.create(CallContextProcessorMediator.class);

        for (ICallContextEndResolver userContextFinishResolver : this.userContextFinishResolvers) {
            userContextFinishResolver.resolve(processorMediator);
        }

        CallContextFinishBuilder callContextFinishBuilder = this.coreManager.create(CallContextFinishBuilder.class);

        callContextFinishBuilder.processorMediator = processorMediator;
        callContextFinishBuilder.factory = this;

        return callContextFinishBuilder;
    }
}
