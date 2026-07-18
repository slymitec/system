package indi.sly.system.services.jobs;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.AService;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.environment.containers.KernelSpace;
import indi.sly.system.services.core.environment.values.ServiceKernelExtensionSpace;
import indi.sly.system.services.core.prototypes.TransactionalActionComponent;
import indi.sly.system.services.jobs.instances.prototypes.processors.*;
import indi.sly.system.services.jobs.instances.prototypes.processors.core.CoreManagerTaskInitializer;
import indi.sly.system.services.jobs.instances.prototypes.processors.core.DateTimeObjectTaskInitializer;
import indi.sly.system.services.jobs.instances.prototypes.processors.core.SystemVersionObjectTaskInitializer;
import indi.sly.system.services.jobs.instances.prototypes.processors.objects.DumpObjectTaskInitializer;
import indi.sly.system.services.jobs.instances.prototypes.processors.objects.InfoObjectTaskInitializer;
import indi.sly.system.services.jobs.instances.prototypes.processors.objects.ObjectManagerTaskInitializer;
import indi.sly.system.services.jobs.instances.prototypes.processors.objects.SecurityDescriptorObjectTaskInitializer;
import indi.sly.system.services.jobs.instances.prototypes.processors.objects.instances.NoneInfoContentObjectTaskInitializer;
import indi.sly.system.services.jobs.instances.prototypes.processors.processes.*;
import indi.sly.system.services.jobs.instances.prototypes.processors.security.*;
import indi.sly.system.services.jobs.instances.prototypes.processors.security.instances.AuditContentObjectTaskInitializer;
import indi.sly.system.services.jobs.instances.prototypes.processors.services.ServicesManagerTaskInitializer;
import indi.sly.system.services.jobs.instances.prototypes.processors.services.instances.ServiceContentObjectTaskInitializer;
import indi.sly.system.services.jobs.prototypes.*;
import indi.sly.system.services.jobs.values.TaskAttributeType;
import indi.sly.system.services.jobs.values.TaskDefinition;
import indi.sly.system.services.jobs.values.ClientRequestRecord;
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
            this.factory = this.coreManager.create(JobFactory.class);
            this.factory.init();
        } else if (startup == StartupType.STEP_INIT_SERVICE) {
            KernelSpace kernelSpace = this.coreManager.getKernelSpace();

            ServiceKernelExtensionSpace serviceSpace = new ServiceKernelExtensionSpace();
            TransactionalActionComponent transactionalAction = this.coreManager.create(TransactionalActionComponent.class);
            serviceSpace.setTransactionalAction(transactionalAction);
            kernelSpace.setServiceSpace(serviceSpace);

            this.createTask("CoreManager", TaskAttributeType.OBJECT_IS_NOT_CACHEABLE, null, this.coreManager.create(CoreManagerTaskInitializer.class));
            this.createTask("SystemVersionObject", TaskAttributeType.NULL, null, this.coreManager.create(SystemVersionObjectTaskInitializer.class));
            this.createTask("DateTimeObject", TaskAttributeType.NULL, null, this.coreManager.create(DateTimeObjectTaskInitializer.class));

            this.createTask("ObjectManager", TaskAttributeType.OBJECT_IS_NOT_CACHEABLE, null, this.coreManager.create(ObjectManagerTaskInitializer.class));
            this.createTask("DumpObject", TaskAttributeType.NULL, null, this.coreManager.create(DumpObjectTaskInitializer.class));
            this.createTask("InfoObject", TaskAttributeType.NULL, null, this.coreManager.create(InfoObjectTaskInitializer.class));
            this.createTask("SecurityDescriptorObject", TaskAttributeType.NULL, null, this.coreManager.create(SecurityDescriptorObjectTaskInitializer.class));
            this.createTask("InfoContentObject", TaskAttributeType.NULL, null, this.coreManager.create(NoneInfoContentObjectTaskInitializer.class));
            this.createTask("FolderContentObject", TaskAttributeType.NULL, null, this.coreManager.create(NoneInfoContentObjectTaskInitializer.class));
            this.createTask("NamelessFolderContentObject", TaskAttributeType.NULL, null, this.coreManager.create(NoneInfoContentObjectTaskInitializer.class));

            this.createTask("ProcessManager", TaskAttributeType.OBJECT_IS_NOT_CACHEABLE, null, this.coreManager.create(ProcessManagerTaskInitializer.class));
            this.createTask("ProcessObject", TaskAttributeType.NULL, null, this.coreManager.create(ProcessObjectTaskInitializer.class));
            this.createTask("ProcessCommunicationObject", TaskAttributeType.NULL, null, this.coreManager.create(ProcessCommunicationObjectTaskInitializer.class));
            this.createTask("ProcessContextObject", TaskAttributeType.NULL, null, this.coreManager.create(ProcessContextObjectTaskInitializer.class));
            this.createTask("ProcessInfoEntryObject", TaskAttributeType.NULL, null, this.coreManager.create(ProcessInfoEntryObjectTaskInitializer.class));
            this.createTask("ProcessInfoTableObject", TaskAttributeType.NULL, null, this.coreManager.create(ProcessInfoTableObjectTaskInitializer.class));
            this.createTask("ProcessSessionObject", TaskAttributeType.NULL, null, this.coreManager.create(ProcessSessionObjectTaskInitializer.class));
            this.createTask("ProcessStatisticsObject", TaskAttributeType.NULL, null, this.coreManager.create(ProcessStatisticsObjectTaskInitializer.class));
            this.createTask("ProcessStatusObject", TaskAttributeType.NULL, null, this.coreManager.create(ProcessStatusObjectTaskInitializer.class));
            this.createTask("ProcessTokenObject", TaskAttributeType.NULL, null, this.coreManager.create(ProcessTokenObjectTaskInitializer.class));

            this.createTask("UserManager", TaskAttributeType.OBJECT_IS_NOT_CACHEABLE, null, this.coreManager.create(UserManagerTaskInitializer.class));
            this.createTask("AccountAuthorization", TaskAttributeType.NULL, null, this.coreManager.create(AccountAuthorizationObjectTaskInitializer.class));
            this.createTask("AccountObject", TaskAttributeType.NULL, null, this.coreManager.create(AccountObjectTaskInitializer.class));
            this.createTask("AccountSessionsObject", TaskAttributeType.NULL, null, this.coreManager.create(AccountSessionsObjectTaskInitializer.class));
            this.createTask("AccountTokenObject", TaskAttributeType.NULL, null, this.coreManager.create(AccountTokenObjectTaskInitializer.class));
            this.createTask("GroupObject", TaskAttributeType.NULL, null, this.coreManager.create(GroupObjectTaskInitializer.class));
            this.createTask("GroupTokenObject", TaskAttributeType.NULL, null, this.coreManager.create(GroupTokenObjectTaskInitializer.class));
            this.createTask("AuditContentObject", TaskAttributeType.NULL, null, this.coreManager.create(AuditContentObjectTaskInitializer.class));

            this.createTask("ServicesManager", TaskAttributeType.OBJECT_IS_NOT_CACHEABLE, null, this.coreManager.create(ServicesManagerTaskInitializer.class));
            this.createTask("ServiceContentObject", TaskAttributeType.NULL, null, this.coreManager.create(ServiceContentObjectTaskInitializer.class));
        }
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void check() {
    }

    protected JobFactory factory;

    private void createTask(String name, long attribute, UUID processId, ATaskInitializer initializer) {
        if (StringUtil.isNameIllegal(name) || ObjectUtil.isAnyNull(initializer)) {
            throw new ConditionParametersException();
        }

        TaskBuilder taskBuilder = this.factory.createTask();

        taskBuilder.create(name, attribute, processId, initializer);
    }

    public TaskObject getTask(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        ServiceKernelExtensionSpace serviceSpace = (ServiceKernelExtensionSpace) this.coreManager.getKernelSpace().getServiceSpace();

        UUID taskID = serviceSpace.getNamedTaskIds().getOrDefault(name, null);

        if (ValueUtil.isAnyNullOrEmpty(taskID)) {
            throw new StatusNotExistedException();
        }

        TaskDefinition task = serviceSpace.getTasks().getOrDefault(taskID, null);

        if (ObjectUtil.isAnyNull(task)) {
            throw new StatusNotExistedException();
        }

        return this.factory.buildTask(task);
    }

    public UserContextObject createUserContext(ClientRequestRecord clientRequest) {
        if (ObjectUtil.isAnyNull(clientRequest)) {
            throw new ConditionParametersException();
        }

        UserContextCreateBuilder userContextCreateBuilder = this.factory.createUserContextCreator();

        return userContextCreateBuilder.create(clientRequest);
    }

    public void finishUserContext(UserContextObject userContext) {
        if (ObjectUtil.isAnyNull(userContext)) {
            throw new ConditionParametersException();
        }

        UserContextFinishBuilder userContextFinishBuilder = this.factory.createUserContextFinish();

        userContextFinishBuilder.finish(userContext);
    }
}
