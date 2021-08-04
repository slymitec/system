package indi.sly.system.services.center.instances.prototypes.processors;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.supports.UUIDUtil;
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
public class RuntimeCenterInitializer extends ACenterInitializer {
    public RuntimeCenterInitializer() {
        this.register("createSession", this::createSession);
    }

    @Override
    public void start(CenterDefinition center) {
    }

    @Override
    public void finish(CenterDefinition center) {
    }

    private void createSession(CenterRunConsumer run, CenterContentObject content) {
        String parameter_AccountName = content.getDatum(String.class, "Security_Account_Name");
        String parameter_AccountPassword = content.getDatum(String.class, "Security_Account_Password");
        long parameter_SessionType = content.getDatumOrDefault(Long.class, "Process_Session_Type", SessionType.CLI);

        //

        ObjectManager objectManager = this.factoryManager.getManager(ObjectManager.class);
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);
        SessionManager sessionManager = this.factoryManager.getManager(SessionManager.class);
        UserManager userManager = this.factoryManager.getManager(UserManager.class);

        //

        AccountAuthorizationObject accountAuthorization = userManager.authorize(parameter_AccountName, parameter_AccountPassword);
        AccountAuthorizationResultDefinition accountAuthorizationResult = accountAuthorization.checkAndGetResult();

        //

        UUID sessionID = sessionManager.create(accountAuthorization);
        SessionContentObject sessionContent = sessionManager.getAndOpen(sessionID);
        if (LogicalUtil.allNotEqual(parameter_SessionType, SessionType.API, SessionType.CLI, SessionType.GUI)) {
            sessionContent.setType(SessionType.CLI);
        } else {
            sessionContent.setType(parameter_SessionType);
        }
        Map<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("AccountID", UUIDUtil.toString(accountAuthorizationResult.getAccountID()));
        environmentVariables.put("Home", "/Files/Main/Users/" + accountAuthorizationResult.getAccountName());
        sessionContent.setEenvironmentVariables(environmentVariables);
        environmentVariables.put("Path", "/Files/Main/System/Bin;/Files/Main/Users/" + accountAuthorizationResult.getAccountName() + "/Bin");
        sessionContent.setEenvironmentVariables(environmentVariables);

        content.setDatum("Processes_Session_ID", sessionID);

        //创建用户态进程 UserSession.bin

        InfoObject userSessionInfo = objectManager.get(List.of(new IdentificationDefinition("Files"),
                new IdentificationDefinition("Main"), new IdentificationDefinition("System"),
                new IdentificationDefinition("Bins"), new IdentificationDefinition("UserSession.bin")));
        UUID userSessionHandle = userSessionInfo.open(InfoOpenAttributeType.OPEN_ONLY_READ);

        ProcessObject process = processManager.create(accountAuthorization, null, userSessionHandle, null,
                StringUtil.EMPTY, PrivilegeType.NULL, List.of(new IdentificationDefinition("Files"),
                        new IdentificationDefinition("Main"), new IdentificationDefinition("Users"),
                        new IdentificationDefinition(accountAuthorizationResult.getAccountName())));

        content.setDatum("Processes_Process_UserSession_ID", process.getID());
        /*
        1.验证用户
        2.创建session
        3.创建用户进程
        */
    }


}
