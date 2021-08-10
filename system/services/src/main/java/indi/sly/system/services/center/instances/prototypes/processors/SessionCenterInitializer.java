package indi.sly.system.services.center.instances.prototypes.processors;

import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.objects.values.InfoOpenAttributeType;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.SessionManager;
import indi.sly.system.kernel.processes.instances.prototypes.SessionContentObject;
import indi.sly.system.kernel.processes.instances.values.SessionType;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.prototypes.AccountObject;
import indi.sly.system.kernel.security.values.AccountAuthorizationResultDefinition;
import indi.sly.system.kernel.security.values.PrivilegeType;
import indi.sly.system.services.center.lang.CenterRunConsumer;
import indi.sly.system.services.center.prototypes.CenterContentObject;
import indi.sly.system.services.center.prototypes.processors.ACenterInitializer;
import indi.sly.system.services.center.values.CenterDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionCenterInitializer extends ACenterInitializer {
    public SessionCenterInitializer() {
        this.register("createSession", this::createSession);
        this.register("deleteSession", this::deleteSession);
        this.register("authorize", this::authorize);
        this.register("createUserSessionBin", this::createUserSessionBin);
        this.register("endUserSessionBin", this::endUserSessionBin);
    }

    @Override
    public void start(CenterDefinition center) {
    }

    @Override
    public void finish(CenterDefinition center) {
    }

    private void createSession(CenterRunConsumer run, CenterContentObject content) {
        long parameter_SessionType = content.getDatumOrDefault(Long.class, "Process_Session_Type", SessionType.CLI);

        //

        SessionManager sessionManager = this.factoryManager.getManager(SessionManager.class);

        //

        UUID sessionID = sessionManager.create();
        SessionContentObject sessionContent = sessionManager.getAndOpen(sessionID);

        if (LogicalUtil.allNotEqual(parameter_SessionType, SessionType.API, SessionType.CLI, SessionType.GUI)) {
            sessionContent.setType(SessionType.CLI);
        } else {
            sessionContent.setType(parameter_SessionType);
        }
        Map<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("Path", "/Files/Main/System/Bin;");
        sessionContent.setEnvironmentVariables(environmentVariables);

        sessionContent.close();

        content.setDatum("Processes_Session_ID", sessionID);
    }

    private void deleteSession(CenterRunConsumer run, CenterContentObject content) {
        UUID parameter_SessionID = content.getDatum(UUID.class, "Processes_Session_ID");

        //

        SessionManager sessionManager = this.factoryManager.getManager(SessionManager.class);

        //

        sessionManager.delete(parameter_SessionID);

        content.deleteDatumIfExisted("Processes_Session_ID");
    }

    private void authorize(CenterRunConsumer run, CenterContentObject content) {
        UUID parameter_SessionID = content.getDatum(UUID.class, "Processes_Session_ID");
        String parameter_AccountName = content.getDatum(String.class, "Security_Account_Name");
        String parameter_AccountPassword = content.getDatum(String.class, "Security_Account_Password");

        //

        SessionManager sessionManager = this.factoryManager.getManager(SessionManager.class);
        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        //

        AccountAuthorizationObject accountAuthorization = userManager.authorize(parameter_AccountName, parameter_AccountPassword);
        AccountAuthorizationResultDefinition accountAuthorizationResult = accountAuthorization.checkAndGetResult();

        SessionContentObject sessionContent = sessionManager.getAndOpen(parameter_SessionID);

        Map<String, String> environmentVariables = new HashMap<>(sessionContent.getEnvironmentVariables());
        environmentVariables.put("AccountID", UUIDUtil.toString(accountAuthorizationResult.getID()));
        environmentVariables.put("Home", "/Files/Main/Users/" + accountAuthorizationResult.getName());
        environmentVariables.put("Path", environmentVariables.getOrDefault("Path", StringUtil.EMPTY) + "/Files/Main/Users/" + accountAuthorizationResult.getName() + "/Bin;");
        sessionContent.setEnvironmentVariables(environmentVariables);

        sessionContent.close();
    }

    private void createUserSessionBin(CenterRunConsumer run, CenterContentObject content) {
        UUID parameter_SessionID = content.getDatum(UUID.class, "Processes_Session_ID");

        //

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        SessionManager sessionManager = this.factoryManager.getManager(SessionManager.class);
        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        //

        SessionContentObject sessionContent = sessionManager.getAndOpen(parameter_SessionID);

        UUID accountID = sessionContent.getAccountID();

        sessionContent.close();

        if (ValueUtil.isAnyNullOrEmpty(accountID)) {
            throw new StatusNotReadyException();
        }

        AccountObject account = userManager.getAccount(accountID);

        InfoObject userSessionInfo = objectManager.get(List.of(new IdentificationDefinition("Files"),
                new IdentificationDefinition("Main"), new IdentificationDefinition("System"),
                new IdentificationDefinition("Bins"), new IdentificationDefinition("UserSession.bin")));
        UUID userSessionHandle = userSessionInfo.open(InfoOpenAttributeType.OPEN_ONLY_READ);

        ProcessObject process = processManager.create(userManager.authorize(accountID), null, userSessionHandle, null,
                StringUtil.EMPTY, PrivilegeType.NULL, List.of(new IdentificationDefinition("Files"),
                        new IdentificationDefinition("Main"), new IdentificationDefinition("Users"),
                        new IdentificationDefinition(account.getName())));

        content.setDatum("Processes_Process_UserSession_ID", process.getID());
    }

    private void endUserSessionBin(CenterRunConsumer run, CenterContentObject content) {
        UUID parameter_ProcessID = content.getDatum(UUID.class, "Processes_Process_UserSession_ID");

        //

        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        //

        processManager.end(parameter_ProcessID);
    }
}
