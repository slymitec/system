package indi.sly.system.services.job.instances.prototypes.processors;

import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.services.job.lang.JobRunConsumer;
import indi.sly.system.services.job.prototypes.JobContentObject;
import indi.sly.system.services.job.prototypes.processors.AJobInitializer;
import indi.sly.system.services.job.values.JobDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionJobInitializer extends AJobInitializer {
    public SessionJobInitializer() {
        this.register("createSession", this::createSession);
        this.register("deleteSession", this::deleteSession);
        this.register("authorize", this::authorize);
        this.register("createUserSessionBin", this::createUserSessionBin);
        this.register("endUserSessionBin", this::endUserSessionBin);
    }

    @Override
    public void start(JobDefinition job) {
    }

    @Override
    public void finish(JobDefinition job) {
    }

    private void createSession(JobRunConsumer run, JobContentObject content) {
//        long parameter_SessionType = content.getParameterOrDefault(Long.class, "Process_Session_Type", SessionType.CLI);
//
//        //
//
//        SessionManager sessionManager = this.factoryManager.getManager(SessionManager.class);
//
//        //
//
//        UUID sessionID = sessionManager.create();
//        SessionContentObject sessionContent = sessionManager.getAndOpen(sessionID);
//
//        if (LogicalUtil.allNotEqual(parameter_SessionType, SessionType.API, SessionType.CLI, SessionType.GUI)) {
//            sessionContent.setType(SessionType.CLI);
//        } else {
//            sessionContent.setType(parameter_SessionType);
//        }
//        Map<String, String> environmentVariables = new HashMap<>();
//        environmentVariables.put("Path", "/Files/Main/System/Bin;");
//        sessionContent.setEnvironmentVariables(environmentVariables);
//
//        sessionContent.close();
//
//        content.setResult("Processes_Session_ID", sessionID);
    }

    private void deleteSession(JobRunConsumer run, JobContentObject content) {
//        UUID parameter_SessionID = content.getParameter(UUID.class, "Processes_Session_ID");
//
//        //
//
//        SessionManager sessionManager = this.factoryManager.getManager(SessionManager.class);
//
//        //
//
//        sessionManager.delete(parameter_SessionID);
    }

    private void authorize(JobRunConsumer run, JobContentObject content) {
//        UUID parameter_SessionID = content.getParameter(UUID.class, "Processes_Session_ID");
//        String parameter_AccountName = content.getParameter(String.class, "Security_Account_Name");
//        String parameter_AccountPassword = content.getParameter(String.class, "Security_Account_Password");
//
//        //
//
//        SessionManager sessionManager = this.factoryManager.getManager(SessionManager.class);
//        UserManager userManager = this.factoryManager.getManager(UserManager.class);
//
//        //
//
//        AccountAuthorizationObject accountAuthorization = userManager.authorize(parameter_AccountName, parameter_AccountPassword);
//        AccountAuthorizationResultDefinition accountAuthorizationResult = accountAuthorization.checkAndGetResult();
//
//        SessionContentObject sessionContent = sessionManager.getAndOpen(parameter_SessionID);
//
//        Map<String, String> environmentVariables = new HashMap<>(sessionContent.getEnvironmentVariables());
//        environmentVariables.put("AccountID", UUIDUtil.toString(accountAuthorizationResult.getID()));
//        environmentVariables.put("Home", "/Files/Main/Users/" + accountAuthorizationResult.getName());
//        environmentVariables.put("Path", environmentVariables.getOrDefault("Path", StringUtil.EMPTY) + "/Files/Main/Users/" + accountAuthorizationResult.getName() + "/Bin;");
//        sessionContent.setEnvironmentVariables(environmentVariables);
//
//        sessionContent.close();
    }

    private void createUserSessionBin(JobRunConsumer run, JobContentObject content) {
//        UUID parameter_SessionID = content.getParameter(UUID.class, "Processes_Session_ID");
//
//        //
//
//        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
//        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
//        SessionManager sessionManager = this.factoryManager.getManager(SessionManager.class);
//        UserManager userManager = this.factoryManager.getManager(UserManager.class);
//
//        //
//
//        SessionContentObject sessionContent = sessionManager.getAndOpen(parameter_SessionID);
//
//        UUID accountID = sessionContent.getAccountID();
//
//        sessionContent.close();
//
//        if (ValueUtil.isAnyNullOrEmpty(accountID)) {
//            throw new StatusNotReadyException();
//        }
//
//        AccountObject account = userManager.getAccount(accountID);
//
//        InfoObject userSessionInfo = objectManager.get(List.of(new IdentificationDefinition("Files"),
//                new IdentificationDefinition("Main"), new IdentificationDefinition("System"),
//                new IdentificationDefinition("Bins"), new IdentificationDefinition("UserSession.bin")));
//        UUID userSessionIndex = userSessionInfo.open(InfoOpenAttributeType.OPEN_ONLY_READ);
//
//        ProcessObject process = processManager.create(userManager.authorize(accountID), PrivilegeType.NULL, null,
//                userSessionIndex, StringUtil.EMPTY, List.of(new IdentificationDefinition("Files"),
//                        new IdentificationDefinition("Main"), new IdentificationDefinition("Users"),
//                        new IdentificationDefinition(account.getName())));
//
//        content.setResult("Processes_Process_UserSession_ID", process.getID());
    }

    private void endUserSessionBin(JobRunConsumer run, JobContentObject content) {
        UUID parameter_ProcessID = content.getParameter(UUID.class, "Processes_Process_UserSession_ID");

        //

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        //

        //processManager.end(parameter_ProcessID);
    }
}
