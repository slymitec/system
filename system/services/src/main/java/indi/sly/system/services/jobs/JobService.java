package indi.sly.system.services.jobs;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.lang.StatusUnreadableException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.AService;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.services.core.environment.values.ServiceKernelSpaceExtensionDefinition;
import indi.sly.system.services.jobs.instances.prototypes.processors.*;
import indi.sly.system.services.jobs.prototypes.*;
import indi.sly.system.services.jobs.values.TaskAttributeType;
import indi.sly.system.services.jobs.values.TaskDefinition;
import indi.sly.system.services.jobs.values.UserContextRequestDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobService extends AService {
    @Override
    public void startup(long startup) {
        if (startup == StartupType.STEP_INIT_SELF) {
            this.factory = this.factoryManager.create(JobFactory.class);
            this.factory.init();
        } else if (startup == StartupType.STEP_INIT_SERVICE) {
            KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();
            KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

            kernelSpace.setServiceSpace(new ServiceKernelSpaceExtensionDefinition());

            this.createTask("FactoryManager", TaskAttributeType.NULL, null, this.factoryManager.create(FactoryManagerTaskInitializer.class));
            this.createTask("ObjectManager", TaskAttributeType.NULL, null, this.factoryManager.create(ObjectManagerTaskInitializer.class));
            this.createTask("ProcessManager", TaskAttributeType.NULL, null, this.factoryManager.create(ProcessTaskInitializer.class));
            this.createTask("UserManager", TaskAttributeType.NULL, null, this.factoryManager.create(UserManagerTaskInitializer.class));

            this.createTask("HandleAction", TaskAttributeType.NULL, null, this.factoryManager.create(HandleActionTaskInitializer.class));
        }
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void check() {
    }

    protected JobFactory factory;

    public void createTask(String name, long attribute, UUID processID, ATaskInitializer initializer) {
        if (StringUtil.isNameIllegal(name) || ObjectUtil.isAnyNull(initializer)) {
            throw new ConditionParametersException();
        }

        TaskBuilder taskBuilder = this.factory.createTask();

        taskBuilder.create(name, attribute, processID, initializer);
    }

    public void deleteTask(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        TaskBuilder taskBuilder = this.factory.createTask();

        taskBuilder.delete(name);
    }

    public TaskObject getTask(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        ServiceKernelSpaceExtensionDefinition serviceSpace = (ServiceKernelSpaceExtensionDefinition) this.factoryManager.getKernelSpace().getServiceSpace();

        UUID taskID = serviceSpace.getNamedTaskIDs().getOrDefault(name, null);

        if (ValueUtil.isAnyNullOrEmpty(taskID)) {
            throw new StatusNotExistedException();
        }

        TaskDefinition task = serviceSpace.getTasks().getOrDefault(taskID, null);

        if (ObjectUtil.isAnyNull(task)) {
            throw new StatusNotExistedException();
        }

        return this.factory.buildTask(task);
    }

    public UserContextObject createUserContext(String userContextRequestRaw) {
        if (ValueUtil.isAnyNullOrEmpty(userContextRequestRaw)) {
            throw new StatusUnreadableException();
        }

        UserContextRequestDefinition userContextRequest =
                ObjectUtil.transferFromStringOrDefaultProvider(UserContextRequestDefinition.class,
                        userContextRequestRaw, () -> {
                            throw new StatusUnreadableException();
                        });

        UserContextCreateBuilder userContextCreateBuilder = this.factory.createUserContextCreator();

        return userContextCreateBuilder.create(userContextRequest);
    }

    public void finishUserContext(UserContextObject userContext) {
        if (ObjectUtil.isAnyNull(userContext)) {
            throw new ConditionParametersException();
        }

        UserContextFinishBuilder userContextFinishBuilder = this.factory.createUserContextFinish();

        userContextFinishBuilder.finish(userContext);
    }
}
