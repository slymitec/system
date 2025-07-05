package indi.sly.system.services.jobs.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.Consumer1;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.services.jobs.prototypes.processors.*;
import indi.sly.system.services.jobs.prototypes.wrappers.TaskProcessorMediator;
import indi.sly.system.services.jobs.prototypes.wrappers.UserContextProcessorMediator;
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

    protected final List<ATaskResolver> taskResolvers;
    protected final List<AUserContextCreateResolver> userContextCreateResolvers;
    protected final List<AUserContextFinishResolver> userContextFinishResolvers;

    @Override
    public void init() {
        this.taskResolvers.add(this.factoryManager.create(TaskCheckConditionResolver.class));
        this.taskResolvers.add(this.factoryManager.create(TaskContentResolver.class));
        this.taskResolvers.add(this.factoryManager.create(TaskInitializerResolver.class));
        this.taskResolvers.add(this.factoryManager.create(TaskProcessAndThreadResolver.class));
        this.taskResolvers.add(this.factoryManager.create(TaskStatusRuntimeResolver.class));

        this.userContextCreateResolvers.add(this.factoryManager.create(UserContextCreateContentResolver.class));
        this.userContextCreateResolvers.add(this.factoryManager.create(UserContextCreateProcessAndThreadResolver.class));
        this.userContextCreateResolvers.add(this.factoryManager.create(UserContextCreateProcessIDVerificationResolver.class));
        this.userContextFinishResolvers.add(this.factoryManager.create(UserContextFinishProcessAndThreadResolver.class));

        Collections.sort(this.taskResolvers);
        Collections.sort(this.userContextCreateResolvers);
        Collections.sort(this.userContextFinishResolvers);
    }

    private TaskObject buildTask(TaskProcessorMediator processorMediator, Provider<TaskDefinition> funcRead,
                                 Consumer1<TaskDefinition> funcWrite) {
        TaskObject task = this.factoryManager.create(TaskObject.class);

        task.setSource(funcRead, funcWrite);
        task.processorMediator = processorMediator;
        task.status = new TaskStatusDefinition();

        return task;
    }

    public TaskObject buildTask(TaskDefinition task) {
        if (ObjectUtil.isAnyNull(task)) {
            throw new ConditionParametersException();
        }

        TaskProcessorMediator processorMediator = this.factoryManager.create(TaskProcessorMediator.class);
        for (ATaskResolver resolver : this.taskResolvers) {
            resolver.resolve(task, processorMediator);
        }

        return this.buildTask(processorMediator, () -> task, (source) -> {
        });
    }

    public TaskBuilder createTask() {
        TaskBuilder taskBuilder = this.factoryManager.create(TaskBuilder.class);

        taskBuilder.factory = this;

        return taskBuilder;
    }

    private UserContextObject buildUserContext(Provider<UserContextDefinition> funcRead, Consumer1<UserContextDefinition> funcWrite) {
        UserContextObject userContext = this.factoryManager.create(UserContextObject.class);

        userContext.setSource(funcRead, funcWrite);

        return userContext;
    }

    public UserContextObject buildUserContext(UserContextDefinition userContext) {
        if (ObjectUtil.isAnyNull(userContext)) {
            throw new ConditionParametersException();
        }

        return this.buildUserContext(() -> userContext, (source) -> {
        });
    }

    public UserContextCreateBuilder createUserContextCreator() {
        UserContextProcessorMediator processorMediator = this.factoryManager.create(UserContextProcessorMediator.class);

        for (AUserContextCreateResolver userContextCreateResolver : this.userContextCreateResolvers) {
            userContextCreateResolver.resolve(processorMediator);
        }

        UserContextCreateBuilder userContextCreateBuilder = this.factoryManager.create(UserContextCreateBuilder.class);

        userContextCreateBuilder.processorMediator = processorMediator;
        userContextCreateBuilder.factory = this;

        return userContextCreateBuilder;
    }

    public UserContextFinishBuilder createUserContextFinish() {
        UserContextProcessorMediator processorMediator = this.factoryManager.create(UserContextProcessorMediator.class);

        for (AUserContextFinishResolver userContextFinishResolver : this.userContextFinishResolvers) {
            userContextFinishResolver.resolve(processorMediator);
        }

        UserContextFinishBuilder userContextFinishBuilder = this.factoryManager.create(UserContextFinishBuilder.class);

        userContextFinishBuilder.processorMediator = processorMediator;
        userContextFinishBuilder.factory = this;

        return userContextFinishBuilder;
    }
}
