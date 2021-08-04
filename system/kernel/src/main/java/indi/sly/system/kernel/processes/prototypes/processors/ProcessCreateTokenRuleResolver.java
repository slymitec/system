package indi.sly.system.kernel.processes.prototypes.processors;

import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.SessionManager;
import indi.sly.system.kernel.processes.instances.prototypes.SessionContentObject;
import indi.sly.system.kernel.processes.instances.values.SessionType;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorCreateFunction;
import indi.sly.system.kernel.processes.prototypes.ProcessContextObject;
import indi.sly.system.kernel.processes.prototypes.ProcessSessionObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessLifeProcessorMediator;
import indi.sly.system.kernel.processes.values.ApplicationDefinition;
import indi.sly.system.kernel.processes.values.ProcessContextType;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.prototypes.AccountObject;
import indi.sly.system.kernel.security.values.AccountAuthorizationResultDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreateTokenRuleResolver extends APrototype implements IProcessCreateResolver {
    private final ProcessLifeProcessorCreateFunction create;

    public ProcessCreateTokenRuleResolver() {
        this.create = (process, parentProcess, processCreator) -> {
            KernelConfigurationDefinition configuration = this.factoryManager.getKernelSpace().getConfiguration();

            ProcessContextObject processContext = process.getContext();
            ProcessSessionObject processSession = process.getSession();
            ProcessTokenObject processToken = process.getToken();
            ProcessTokenObject parentProcessToken = parentProcess.getToken();

            Set<UUID> roles = new HashSet<>(processToken.getRoles());
            long processContextType = processContext.getType();
            if (processContextType == ProcessContextType.SERVICE) {
                ApplicationDefinition processContextApplication = processContext.getApplication();
                if (ObjectUtil.allNotNull(processContextApplication)) {
                    roles.add(processContextApplication.getID());
                }
            } else if (processContextType == ProcessContextType.BATCH) {
                roles.add(configuration.SECURITY_ROLE_BATCHES_ID);
            } else if (processContextType == ProcessContextType.EXECUTABLE) {
                roles.add(configuration.SECURITY_ROLE_EXECUTABLE_ID);
            }

            if (parentProcessToken.getAccountID().equals(processToken.getAccountID())) {
                UserManager userManager = this.factoryManager.getManager(UserManager.class);

                AccountObject account = userManager.getCurrentAccount();
                if (ValueUtil.isAnyNullOrEmpty(account.getPassword())) {
                    roles.add(configuration.SECURITY_ROLE_EMPTY_PASSWORD_ID);
                }
            } else {
                AccountAuthorizationObject accountAuthorization = processCreator.getAccountAuthorization();
                AccountAuthorizationResultDefinition accountAuthorizationResult = accountAuthorization.checkAndGetResult();
                if (ValueUtil.isAnyNullOrEmpty(accountAuthorizationResult.getPassword())) {
                    roles.add(configuration.SECURITY_ROLE_EMPTY_PASSWORD_ID);
                }
            }

            SessionManager sessionManager = this.factoryManager.getManager(SessionManager.class);
            SessionContentObject sessionContent = sessionManager.getAndOpen(processSession.getID());
            long sessionContentType = sessionContent.getType();
            if (LogicalUtil.isAnyExist(sessionContentType, SessionType.API)) {
                roles.add(configuration.SECURITY_ROLE_API_ID);
            } else if (LogicalUtil.isAnyExist(sessionContentType, SessionType.GUI)) {
                roles.add(configuration.SECURITY_ROLE_GUI_ID);
            } else if (LogicalUtil.isAnyExist(sessionContentType, SessionType.CLI)) {
                roles.add(configuration.SECURITY_ROLE_CLI_ID);
            }
            sessionContent.close();

            if (ObjectUtil.allNotNull(processCreator.getAdditionalRoles())) {
                roles.addAll(processCreator.getAdditionalRoles());
            }

            processToken.setRoles(roles);

            return process;
        };
    }

    @Override
    public int order() {
        return 3;
    }

    @Override
    public void resolve(ProcessLifeProcessorMediator processorCreatorMediator) {
        processorCreatorMediator.getCreates().add(create);
    }
}
