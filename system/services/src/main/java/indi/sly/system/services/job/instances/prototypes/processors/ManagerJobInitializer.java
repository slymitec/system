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
import indi.sly.system.services.job.lang.JobRunConsumer;
import indi.sly.system.services.job.prototypes.JobContentObject;
import indi.sly.system.services.job.prototypes.processors.AJobInitializer;
import indi.sly.system.services.job.values.JobDefinition;
import indi.sly.system.services.job.values.JobTransactionType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.Map;
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
        //this.register("processEnd", this::processEnd, JobTransactionType.INDEPENDENCE);

        this.register("sessionGetAndOpen", this::sessionGetAndOpen, JobTransactionType.INDEPENDENCE);
        this.register("sessionEnd", this::sessionClose, JobTransactionType.INDEPENDENCE);

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
        UUID parameter_ProcessID = content.getParameterOrDefault(UUID.class, "processID", null);
        if (ValueUtil.isAnyNullOrEmpty(parameter_ProcessID)) {
            throw new ConditionParametersException();
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process;
        UUID parameter_accountAuthorizationID = content.getParameterOrDefault(UUID.class, "accountAuthorizationID", null);
        if (ValueUtil.isAnyNullOrEmpty(parameter_accountAuthorizationID)) {
            process = processManager.get(parameter_ProcessID);
        } else {
            AccountAuthorizationObject accountAuthorization = content.getCache(parameter_accountAuthorizationID);
            process = processManager.get(parameter_ProcessID, accountAuthorization);
        }

        UUID handle = process.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    @SuppressWarnings("unchecked")
    private void processCreate(JobRunConsumer run, JobContentObject content) {
        UUID parameter_accountAuthorizationID = content.getParameterOrDefault(UUID.class, "accountAuthorizationID", null);
        AccountAuthorizationObject accountAuthorization = null;
        if (ValueUtil.isAnyNullOrEmpty(parameter_accountAuthorizationID)) {
            accountAuthorization = content.getCache(parameter_accountAuthorizationID);
        }
        UUID parameter_FileIndex = content.getParameterOrDefault(UUID.class, "fileIndex", null);
        Map<Long, Integer> parameter_Limits = content.getParameterOrDefault(Map.class, "limits", null);
        String parameter_Parameters = content.getParameterOrDefault(String.class, "parameters", null);
        long parameter_Privileges = content.getParameterOrDefault(Long.class, "privileges", null);
        String parameter_WorkFolder = content.getParameterOrDefault(String.class, "workFolder", null);
        List<IdentificationDefinition> workFolder = null;
        if (ObjectUtil.allNotNull(parameter_WorkFolder)) {
            workFolder = StringUtil.parseIdentifications(parameter_WorkFolder);
        }

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.create(accountAuthorization, parameter_Privileges, parameter_Limits,
                parameter_FileIndex, parameter_Parameters, workFolder);

        UUID handle = process.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void processEndCurrent(JobRunConsumer run, JobContentObject content) {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        processManager.endCurrent();
    }

//    private void processEnd(JobRunConsumer run, JobContentObject content) {
//        UUID parameter_ProcessID = content.getParameterOrDefault(UUID.class, "processID", null);
//        if (ValueUtil.isAnyNullOrEmpty(parameter_ProcessID)) {
//            throw new ConditionParametersException();
//        }
//
//        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
//
//        UUID parameter_accountAuthorizationID = content.getParameterOrDefault(UUID.class, "accountAuthorizationID", null);
//        if (ValueUtil.isAnyNullOrEmpty(parameter_accountAuthorizationID)) {
//            processManager.end(parameter_ProcessID);
//        } else {
//            AccountAuthorizationObject accountAuthorization = content.getCache(parameter_accountAuthorizationID);
//            processManager.end(parameter_ProcessID, accountAuthorization);
//        }
//    }

    private void sessionGetAndOpen(JobRunConsumer run, JobContentObject content) {
//        UUID parameter_SessionID = content.getParameterOrDefault(UUID.class, "sessionID", null);
//        if (ValueUtil.isAnyNullOrEmpty(parameter_SessionID)) {
//            throw new ConditionParametersException();
//        }
//
//        SessionManager sessionManager = this.factoryManager.getManager(SessionManager.class);
//
//        SessionContentObject sessionContent = sessionManager.getAndOpen(parameter_SessionID);
//
//        UUID handle = sessionContent.cache(SpaceType.USER);
//        content.setResult("handle", handle);
    }

    private void sessionClose(JobRunConsumer run, JobContentObject content) {
//        UUID parameter_SessionContentID = content.getParameterOrDefault(UUID.class, "sessionContentID", null);
//        SessionContentObject sessionContent;
//        if (ValueUtil.isAnyNullOrEmpty(parameter_SessionContentID)) {
//            throw new ConditionParametersException();
//        } else {
//            sessionContent = content.getCache(parameter_SessionContentID);
//        }
//
//        SessionManager sessionManager = this.factoryManager.getManager(SessionManager.class);
//
//        sessionManager.close(sessionContent);
//
//        content.deleteCache(parameter_SessionContentID);
    }

    private void userGetCurrentAccount(JobRunConsumer run, JobContentObject content) {
        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account = userManager.getCurrentAccount();

        UUID handle = account.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void userGetAccount(JobRunConsumer run, JobContentObject content) {
        UUID parameter_AccountID = content.getParameterOrDefault(UUID.class, "accountID", null);
        String parameter_AccountName = content.getParameterOrDefault(String.class, "accountName", null);

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account;
        if (!ValueUtil.isAnyNullOrEmpty(parameter_AccountID)) {
            account = userManager.getAccount(parameter_AccountID);
        } else if (!StringUtil.isNameIllegal(parameter_AccountName)) {
            account = userManager.getAccount(parameter_AccountName);
        } else {
            throw new ConditionParametersException();
        }

        UUID handle = account.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void userGetGroup(JobRunConsumer run, JobContentObject content) {
        UUID parameter_GroupID = content.getParameterOrDefault(UUID.class, "groupID", null);
        String parameter_GroupName = content.getParameterOrDefault(String.class, "groupName", null);

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        GroupObject group;
        if (!ValueUtil.isAnyNullOrEmpty(parameter_GroupID)) {
            group = userManager.getGroup(parameter_GroupID);
        } else if (!StringUtil.isNameIllegal(parameter_GroupName)) {
            group = userManager.getGroup(parameter_GroupName);
        } else {
            throw new ConditionParametersException();
        }

        UUID handle = group.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void userCreateAccount(JobRunConsumer run, JobContentObject content) {
        String parameter_AccountName = content.getParameterOrDefault(String.class, "accountName", null);
        String parameter_AccountPassword = content.getParameterOrDefault(String.class, "accountPassword", null);

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountObject account = userManager.createAccount(parameter_AccountName, parameter_AccountPassword);

        UUID handle = account.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void userCreateGroup(JobRunConsumer run, JobContentObject content) {
        String parameter_GroupName = content.getParameterOrDefault(String.class, "groupName", null);

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        GroupObject group = userManager.createGroup(parameter_GroupName);

        UUID handle = group.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }

    private void userDeleteAccount(JobRunConsumer run, JobContentObject content) {
        UUID parameter_AccountID = content.getParameterOrDefault(UUID.class, "accountID", null);

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        userManager.deleteAccount(parameter_AccountID);
    }

    private void userDeleteGroup(JobRunConsumer run, JobContentObject content) {
        UUID parameter_GroupID = content.getParameterOrDefault(UUID.class, "groupID", null);

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        userManager.deleteGroup(parameter_GroupID);
    }

    private void userAuthorize(JobRunConsumer run, JobContentObject content) {
        UUID parameter_AccountID = content.getParameterOrDefault(UUID.class, "accountID", null);
        String parameter_AccountName = content.getParameterOrDefault(String.class, "accountName", null);
        String parameter_AccountPassword = content.getParameterOrDefault(String.class, "accountPassword", null);

        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        AccountAuthorizationObject accountAuthorization;
        if (!ValueUtil.isAnyNullOrEmpty(parameter_AccountID)) {
            accountAuthorization = userManager.authorize(parameter_AccountID);
        } else if (!StringUtil.isNameIllegal(parameter_AccountName)) {
            accountAuthorization = userManager.authorize(parameter_AccountName, parameter_AccountPassword);
        } else {
            throw new ConditionParametersException();
        }

        UUID handle = accountAuthorization.cache(SpaceType.USER);
        content.setResult("handle", handle);
    }
}
