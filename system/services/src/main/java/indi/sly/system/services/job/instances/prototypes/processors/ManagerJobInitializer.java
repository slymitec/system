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
import indi.sly.system.services.job.lang.JobRunConsumer;
import indi.sly.system.services.job.prototypes.JobContentObject;
import indi.sly.system.services.job.values.JobDefinition;
import indi.sly.system.services.job.values.JobTransactionType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ManagerJobInitializer extends AJobInitializer {
    public ManagerJobInitializer() {
        this.register("coreGetDate", this::coreGetDate, JobTransactionType.INDEPENDENCE);
        this.register("coreGetVersion", this::coreGetVersion, JobTransactionType.INDEPENDENCE);

        this.register("objectGet", this::objectGet, JobTransactionType.INDEPENDENCE);

        this.register("processGetCurrent", this::processGetCurrent, JobTransactionType.INDEPENDENCE);
        this.register("processGet", this::processGet, JobTransactionType.INDEPENDENCE);
        this.register("processCreate", this::processCreate, JobTransactionType.INDEPENDENCE);
        this.register("processEndCurrent", this::processEndCurrent, JobTransactionType.INDEPENDENCE);

        this.register("userGetCurrentAccount", this::userGetCurrentAccount, JobTransactionType.INDEPENDENCE);
        this.register("userGetAccount", this::userGetAccount, JobTransactionType.INDEPENDENCE);
        this.register("userGetGroup", this::userGetGroup, JobTransactionType.INDEPENDENCE);
        this.register("userCreateAccount", this::userCreateAccount, JobTransactionType.INDEPENDENCE);
        this.register("userCreateGroup", this::userCreateGroup, JobTransactionType.INDEPENDENCE);
        this.register("userDeleteAccount", this::userDeleteAccount, JobTransactionType.INDEPENDENCE);
        this.register("userDeleteGroup", this::userDeleteGroup, JobTransactionType.INDEPENDENCE);
        this.register("userAuthorize", this::userAuthorize, JobTransactionType.INDEPENDENCE);
    }

    @Override
    public void start(JobDefinition job) {
    }

    @Override
    public void finish(JobDefinition job) {
    }

    private void coreGetDate(JobRunConsumer run, JobContentObject content) {
        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, DateTimeObject.class);

        UUID handle = dateTime.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void coreGetVersion(JobRunConsumer run, JobContentObject content) {
        SystemVersionObject systemVersion = this.factoryManager.getCoreObjectRepository().getByClass(SpaceType.KERNEL, SystemVersionObject.class);

        UUID handle = systemVersion.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void objectGet(JobRunConsumer run, JobContentObject content) {
        String parameter_Identifications = content.getParameterOrDefault(String.class, "identifications", null);
        List<IdentificationDefinition> identifications = StringUtil.parseIdentifications(parameter_Identifications);

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);

        InfoObject info = objectManager.get(identifications);

        UUID handle = info.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void processGetCurrent(JobRunConsumer run, JobContentObject content) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrent();

        UUID handle = process.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void processGet(JobRunConsumer run, JobContentObject content) {
        UUID processID = content.getParameterOrDefaultProvider(UUID.class, "processID", () -> {
            throw new ConditionParametersException();
        });
        AccountAuthorizationObject accountAuthorization = content.getCacheFromParameter("accountAuthorizationID");

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

    private void processCreate(JobRunConsumer run, JobContentObject content) {
        AccountAuthorizationObject accountAuthorization = content.getCacheFromParameter("accountAuthorizationID");
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

    private void processEndCurrent(JobRunConsumer run, JobContentObject content) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        processManager.endCurrent();
    }

    private void userGetCurrentAccount(JobRunConsumer run, JobContentObject content) {
        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account = userManager.getCurrentAccount();

        UUID handle = account.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void userGetAccount(JobRunConsumer run, JobContentObject content) {
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

    private void userGetGroup(JobRunConsumer run, JobContentObject content) {
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

    private void userCreateAccount(JobRunConsumer run, JobContentObject content) {
        String accountName = content.getParameterOrDefault(String.class, "accountName", null);
        String accountPassword = content.getParameterOrDefault(String.class, "accountPassword", null);

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account = userManager.createAccount(accountName, accountPassword);

        UUID handle = account.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void userCreateGroup(JobRunConsumer run, JobContentObject content) {
        String groupName = content.getParameterOrDefault(String.class, "groupName", null);

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        GroupObject group = userManager.createGroup(groupName);

        UUID handle = group.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void userDeleteAccount(JobRunConsumer run, JobContentObject content) {
        UUID accountID = content.getParameterOrDefault(UUID.class, "accountID", null);

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        userManager.deleteAccount(accountID);
    }

    private void userDeleteGroup(JobRunConsumer run, JobContentObject content) {
        UUID groupID = content.getParameterOrDefault(UUID.class, "groupID", null);

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        userManager.deleteGroup(groupID);
    }

    private void userAuthorize(JobRunConsumer run, JobContentObject content) {
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
