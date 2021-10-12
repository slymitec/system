package indi.sly.system.services.job.instances.prototypes.processors;

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
import indi.sly.system.services.job.lang.TaskRunConsumer;
import indi.sly.system.services.job.prototypes.TaskContentObject;
import indi.sly.system.services.job.values.TaskDefinition;
import indi.sly.system.services.core.values.TransactionType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
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
        String parameter_Identifications = content.getParameterOrDefault(String.class, "identifications", null);
        List<IdentificationDefinition> identifications = StringUtil.parseIdentifications(parameter_Identifications);

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
        UUID processID = content.getParameterOrDefaultProvider(UUID.class, "processID", () -> {
            throw new ConditionParametersException();
        });
        AccountAuthorizationObject accountAuthorization = content.getCacheByParameterName("accountAuthorizationID");

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
        AccountAuthorizationObject accountAuthorization = content.getCacheByParameterName("accountAuthorizationID");
        UUID fileIndex = content.getParameterOrDefault(UUID.class, "fileIndex", null);
        String parameters = content.getParameterOrDefault(String.class, "parameters", null);
        String parameter_WorkFolder = content.getParameterOrDefault(String.class, "workFolder", null);
        List<IdentificationDefinition> workFolder = null;
        if (ObjectUtil.allNotNull(parameter_WorkFolder)) {
            workFolder = StringUtil.parseIdentifications(parameter_WorkFolder);
        }

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
        UUID accountID = content.getParameterOrDefault(UUID.class, "accountID", null);
        String accountName = content.getParameterOrDefault(String.class, "accountName", null);

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
        UUID groupID = content.getParameterOrDefault(UUID.class, "groupID", null);
        String groupName = content.getParameterOrDefault(String.class, "groupName", null);

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
        String accountName = content.getParameterOrDefault(String.class, "accountName", null);
        String accountPassword = content.getParameterOrDefault(String.class, "accountPassword", null);

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account = userManager.createAccount(accountName, accountPassword);

        UUID handle = account.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void userCreateGroup(TaskRunConsumer run, TaskContentObject content) {
        String groupName = content.getParameterOrDefault(String.class, "groupName", null);

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        GroupObject group = userManager.createGroup(groupName);

        UUID handle = group.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void userDeleteAccount(TaskRunConsumer run, TaskContentObject content) {
        UUID accountID = content.getParameterOrDefault(UUID.class, "accountID", null);

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        userManager.deleteAccount(accountID);
    }

    private void userDeleteGroup(TaskRunConsumer run, TaskContentObject content) {
        UUID groupID = content.getParameterOrDefault(UUID.class, "groupID", null);

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        userManager.deleteGroup(groupID);
    }

    private void userAuthorize(TaskRunConsumer run, TaskContentObject content) {
        UUID accountID = content.getParameterOrDefault(UUID.class, "accountID", null);
        String accountName = content.getParameterOrDefault(String.class, "accountName", null);
        String accountPassword = content.getParameterOrDefault(String.class, "accountPassword", null);
        AccountAuthorizationTokenDefinition accountAuthorizationToken = content.getParameterOrDefault(AccountAuthorizationTokenDefinition.class, "accountAuthorizationToken", null);

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
