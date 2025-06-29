package indi.sly.system.services.jobs.instances.prototypes.processors;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.SystemVersionObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.prototypes.AccountObject;
import indi.sly.system.kernel.security.prototypes.GroupObject;
import indi.sly.system.kernel.security.values.AccountAuthorizationTokenDefinition;
import indi.sly.system.services.core.values.TransactionType;
import indi.sly.system.services.jobs.lang.TaskRunConsumer;
import indi.sly.system.services.jobs.prototypes.TaskContentObject;
import indi.sly.system.services.jobs.values.TaskDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ManagerTaskInitializer extends ATaskInitializer {
    public ManagerTaskInitializer() {
        this.register("coreGetDate", this::coreGetDate, TransactionType.WHATEVER);
        this.register("coreGetVersion", this::coreGetVersion, TransactionType.WHATEVER);

        this.register("objectGet", this::objectGet, TransactionType.INDEPENDENCE);

        this.register("processGetCurrent", this::processGetCurrent, TransactionType.INDEPENDENCE);
        this.register("processGet", this::processGet, TransactionType.INDEPENDENCE);
        this.register("processCreate", this::processCreate, TransactionType.INDEPENDENCE);
        this.register("processEndCurrent", this::processEndCurrent, TransactionType.INDEPENDENCE);

        this.register("userGetCurrentAccount", this::userGetCurrentAccount, TransactionType.INDEPENDENCE);
        this.register("userGetAccount", this::userGetAccount, TransactionType.INDEPENDENCE);
        this.register("userGetGroup", this::userGetGroup, TransactionType.INDEPENDENCE);
        this.register("userCreateAccount", this::userCreateAccount, TransactionType.INDEPENDENCE);
        this.register("userCreateGroup", this::userCreateGroup, TransactionType.INDEPENDENCE);
        this.register("userDeleteAccount", this::userDeleteAccount, TransactionType.INDEPENDENCE);
        this.register("userDeleteGroup", this::userDeleteGroup, TransactionType.INDEPENDENCE);
        this.register("userAuthorize", this::userAuthorize, TransactionType.INDEPENDENCE);
    }

    @Override
    public void start(TaskDefinition task) {
    }

    @Override
    public void finish(TaskDefinition task) {
    }

    private void coreGetDate(TaskRunConsumer run, TaskContentObject content) {
        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);

        UUID handle = dateTime.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void coreGetVersion(TaskRunConsumer run, TaskContentObject content) {
        SystemVersionObject systemVersion = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, SystemVersionObject.class);

        UUID handle = systemVersion.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void objectGet(TaskRunConsumer run, TaskContentObject content) {
        List<IdentificationDefinition> identifications = content.getParameterList(IdentificationDefinition.class, "identifications");

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        InfoObject info = objectManager.get(identifications);

        UUID handle = info.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void processGetCurrent(TaskRunConsumer run, TaskContentObject content) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();

        UUID handle = process.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void processGet(TaskRunConsumer run, TaskContentObject content) {
        UUID processID = content.getParameter(UUID.class, "processID");
        if (ValueUtil.isAnyNullOrEmpty(processID)) {
            throw new ConditionParametersException();
        }

        AccountAuthorizationObject accountAuthorization = content.getCacheByParameterNameOrDefault("accountAuthorizationID", null);

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process;
        if (ObjectUtil.isAnyNull(accountAuthorization)) {
            process = processManager.get(processID);
        } else {
            process = processManager.get(processID, accountAuthorization);
        }

        UUID handle = process.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void processCreate(TaskRunConsumer run, TaskContentObject content) {
        AccountAuthorizationObject accountAuthorization = content.getCacheByParameterNameOrDefault("accountAuthorizationID", null);
        UUID fileIndex = content.getParameterOrNull(UUID.class, "fileIndex");
        String parameters = content.getParameterOrNull("parameters");
        List<IdentificationDefinition> workFolder = content.getParameterListOrNull(IdentificationDefinition.class, "workFolder");

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.create(accountAuthorization, fileIndex, parameters, workFolder);

        UUID handle = process.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void processEndCurrent(TaskRunConsumer run, TaskContentObject content) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        processManager.endCurrent();
    }

    private void userGetCurrentAccount(TaskRunConsumer run, TaskContentObject content) {
        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account = userManager.getCurrentAccount();

        UUID handle = account.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void userGetAccount(TaskRunConsumer run, TaskContentObject content) {
        UUID accountID = content.getParameterOrNull(UUID.class, "accountID");
        String accountName = content.getParameterOrNull("accountName");

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account;
        if (!ValueUtil.isAnyNullOrEmpty(accountID)) {
            account = userManager.getAccount(accountID);
        } else if (!StringUtil.isNameIllegal(accountName)) {
            account = userManager.getAccount(accountName);
        } else {
            throw new ConditionParametersException();
        }

        UUID handle = account.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void userGetGroup(TaskRunConsumer run, TaskContentObject content) {
        UUID groupID = content.getParameterOrNull(UUID.class, "groupID");
        String groupName = content.getParameterOrNull("groupName");

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        GroupObject group;
        if (!ValueUtil.isAnyNullOrEmpty(groupID)) {
            group = userManager.getGroup(groupID);
        } else if (!StringUtil.isNameIllegal(groupName)) {
            group = userManager.getGroup(groupName);
        } else {
            throw new ConditionParametersException();
        }

        UUID handle = group.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void userCreateAccount(TaskRunConsumer run, TaskContentObject content) {
        String accountName = content.getParameterOrNull("accountName");
        String accountPassword = content.getParameterOrNull("accountPassword");

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account = userManager.createAccount(accountName, accountPassword);

        UUID handle = account.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void userCreateGroup(TaskRunConsumer run, TaskContentObject content) {
        String groupName = content.getParameterOrNull("groupName");

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        GroupObject group = userManager.createGroup(groupName);

        UUID handle = group.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void userDeleteAccount(TaskRunConsumer run, TaskContentObject content) {
        UUID accountID = content.getParameterOrNull(UUID.class, "accountID");

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        userManager.deleteAccount(accountID);
    }

    private void userDeleteGroup(TaskRunConsumer run, TaskContentObject content) {
        UUID groupID = content.getParameterOrNull(UUID.class, "groupID");

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        userManager.deleteGroup(groupID);
    }

    private void userAuthorize(TaskRunConsumer run, TaskContentObject content) {
        UUID accountID = content.getParameterOrNull(UUID.class, "accountID");
        String accountName = content.getParameterOrNull("accountName");
        String accountPassword = content.getParameterOrNull("accountPassword");
        AccountAuthorizationTokenDefinition accountAuthorizationToken =
                content.getParameterOrNull(AccountAuthorizationTokenDefinition.class, "accountAuthorizationToken");

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountAuthorizationObject accountAuthorization;
        if (!ValueUtil.isAnyNullOrEmpty(accountID)) {
            accountAuthorization = userManager.authorize(accountID);
        } else if (!StringUtil.isNameIllegal(accountName)) {
            if (ObjectUtil.allNotNull(accountAuthorizationToken)) {
                accountAuthorization = userManager.authorize(accountName, accountPassword, accountAuthorizationToken);
            } else {
                accountAuthorization = userManager.authorize(accountName, accountPassword);
            }
        } else {
            throw new ConditionParametersException();
        }

        UUID handle = accountAuthorization.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }
}
