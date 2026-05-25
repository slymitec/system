package indi.sly.system.services.jobs.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.services.jobs.prototypes.processors.*;
import indi.sly.system.services.jobs.prototypes.mediators.TaskProcessorMediator;
import indi.sly.system.services.jobs.prototypes.mediators.UserContextProcessorMediator;
import indi.sly.system.services.jobs.values.TaskDefinition;
import indi.sly.system.services.jobs.values.TaskStatusDefinition;
import indi.sly.system.services.jobs.values.UserContextDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.Collections;
import java.util.List;
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
    protected final List<IUserContextCreateResolver> userContextCreateResolvers;
    protected final List<IUserContextFinishResolver> userContextFinishResolvers;

    @Override
    public void init() {
        this.taskResolvers.add(this.coreManager.create(TaskCacheableObjectResolver.class));
        this.taskResolvers.add(this.coreManager.create(TaskCheckConditionResolver.class));
        this.taskResolvers.add(this.coreManager.create(TaskContentResolver.class));
        this.taskResolvers.add(this.coreManager.create(TaskDateResolver.class));
        this.taskResolvers.add(this.coreManager.create(TaskInitializerResolver.class));
        this.taskResolvers.add(this.coreManager.create(TaskProcessAndThreadResolver.class));
        this.taskResolvers.add(this.coreManager.create(TaskStatusRuntimeResolver.class));
        
        this.userContextCreateResolvers.add(this.coreManager.create(UserContextCreateContentResolver.class));
        this.userContextCreateResolvers.add(this.coreManager.create(UserContextCreateThreadResolver.class));
        this.userContextCreateResolvers.add(this.coreManager.create(UserContextCreateCheckClientProcessIdResolver.class));
        this.userContextCreateResolvers.add(this.coreManager.create(UserContextCreateCheckProcessResolver.class));

        this.userContextFinishResolvers.add(this.coreManager.create(UserContextFinishThreadResolver.class));

        Collections.sort(this.taskResolvers);
        Collections.sort(this.userContextCreateResolvers);
        Collections.sort(this.userContextFinishResolvers);
    }

    private TaskObject createTask(TaskProcessorMediator processorMediator, TaskDefinition definition) {
        TaskObject task = this.coreManager.create(TaskObject.class);

        task.setDefinition(definition);
        task.processorMediator = processorMediator;
        task.status = new TaskStatusDefinition();

        return task;
    }

    public TaskObject buildTask(TaskDefinition task) {
        if (ObjectUtil.isAnyNull(task)) {
            throw new ConditionParametersException();
        }

        TaskProcessorMediator processorMediator = this.coreManager.create(TaskProcessorMediator.class);
        for (ITaskResolver resolver : this.taskResolvers) {
            resolver.resolve(task, processorMediator);
        }

        return this.createTask(processorMediator, task);
    }

    public TaskBuilder createTask() {
        TaskBuilder taskBuilder = this.coreManager.create(TaskBuilder.class);

        taskBuilder.factory = this;

        return taskBuilder;
    }

    private UserContextObject createUserContext(UserContextDefinition definition) {
        UserContextObject userContext = this.coreManager.create(UserContextObject.class);

        userContext.setDefinition(definition);

        return userContext;
    }

    public UserContextObject buildUserContext(UserContextDefinition userContext) {
        if (ObjectUtil.isAnyNull(userContext)) {
            throw new ConditionParametersException();
        }

        return this.createUserContext(userContext);
    }

    public UserContextCreateBuilder createUserContextCreator() {
        UserContextProcessorMediator processorMediator = this.coreManager.create(UserContextProcessorMediator.class);

        for (IUserContextCreateResolver userContextCreateResolver : this.userContextCreateResolvers) {
            userContextCreateResolver.resolve(processorMediator);
        }

        UserContextCreateBuilder userContextCreateBuilder = this.coreManager.create(UserContextCreateBuilder.class);

        userContextCreateBuilder.processorMediator = processorMediator;
        userContextCreateBuilder.factory = this;

        return userContextCreateBuilder;
    }

    public UserContextFinishBuilder createUserContextFinish() {
        UserContextProcessorMediator processorMediator = this.coreManager.create(UserContextProcessorMediator.class);

        for (IUserContextFinishResolver userContextFinishResolver : this.userContextFinishResolvers) {
            userContextFinishResolver.resolve(processorMediator);
        }

        UserContextFinishBuilder userContextFinishBuilder = this.coreManager.create(UserContextFinishBuilder.class);

        userContextFinishBuilder.processorMediator = processorMediator;
        userContextFinishBuilder.factory = this;

        return userContextFinishBuilder;
    }
}
