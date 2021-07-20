package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.common.supports.*;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeType;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.SessionManager;
import indi.sly.system.kernel.processes.instances.prototypes.SessionContentObject;
import indi.sly.system.kernel.processes.instances.values.SessionType;
import indi.sly.system.kernel.processes.values.*;
import indi.sly.system.kernel.security.UserManager;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.prototypes.AccountObject;
import indi.sly.system.kernel.security.values.PrivilegeType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.*;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreateProcessBuilder extends APrototype {
    public CreateProcessBuilder() {
        this.createProcess = new CreateProcessDefinition();
    }

    protected ProcessFactory processFactory;
    private ProcessObject parentProcess;
    private final CreateProcessDefinition createProcess;
    private ProcessObject process;

    public void setParentProcess(ProcessObject parentProcess) {
        this.parentProcess = parentProcess;
    }

    public CreateProcessBuilder setAccountAuthorization(AccountAuthorizationObject accountAuthorization) {
        if (ObjectUtil.isAnyNull(accountAuthorization)) {
            throw new ConditionParametersException();
        }

        this.createProcess.setAccountAuthorization(accountAuthorization);

        return this;
    }

    public CreateProcessBuilder setEnvironmentVariable(Map<String, String> environmentVariable) {
        if (ObjectUtil.isAnyNull(environmentVariable)) {
            throw new ConditionParametersException();
        }

        this.createProcess.setEnvironmentVariable(environmentVariable);

        return this;
    }

    public CreateProcessBuilder setFileHandle(UUID fileHandle) {
        if (ValueUtil.isAnyNullOrEmpty(fileHandle)) {
            throw new ConditionParametersException();
        }

        this.createProcess.setFileHandle(fileHandle);

        return this;
    }

    public CreateProcessBuilder setLimits(Map<Long, Integer> limits) {
        if (ObjectUtil.isAnyNull(limits)) {
            throw new ConditionParametersException();
        }

        ProcessTokenObject parentProcessToken = this.parentProcess.getToken();
        if (!parentProcessToken.isPrivileges(PrivilegeType.PROCESSES_MODIFY_LIMITS)) {
            throw new ConditionPermissionsException();
        }

        this.createProcess.setLimits(limits);

        return this;
    }

    public CreateProcessBuilder setAdditionalRoles(Set<UUID> roles) {
        if (ObjectUtil.isAnyNull(roles)) {
            throw new ConditionParametersException();
        }

        ProcessTokenObject parentProcessToken = this.parentProcess.getToken();
        if (!parentProcessToken.isPrivileges(PrivilegeType.PROCESSES_ADD_ROLES)) {
            throw new ConditionPermissionsException();
        }

        this.createProcess.setAdditionalRoles(roles);

        return this;
    }

    public CreateProcessBuilder setParameters(Map<String, String> parameters) {
        if (ObjectUtil.isAnyNull(parameters)) {
            throw new ConditionParametersException();
        }

        this.createProcess.setParameters(parameters);

        return this;
    }

    public CreateProcessBuilder setPrivilegeTypes(long privilegeTypes) {
        ProcessTokenObject parentProcessToken = this.parentProcess.getToken();
        if (!parentProcessToken.isPrivileges(PrivilegeType.CORE_MODIFY_PRIVILEGES)) {
            throw new ConditionPermissionsException();
        }

        this.createProcess.setPrivilegeTypes(privilegeTypes);

        return this;
    }

    public CreateProcessBuilder setSessionID(UUID sessionID) {
        if (ValueUtil.isAnyNullOrEmpty(sessionID)) {
            throw new ConditionParametersException();
        }

        ProcessTokenObject parentProcessToken = this.parentProcess.getToken();
        if (!parentProcessToken.isPrivileges(PrivilegeType.SESSION_MODIFY_USERSESSION)) {
            throw new ConditionPermissionsException();
        }

        this.createProcess.setSessionID(sessionID);

        return this;
    }

    public CreateProcessBuilder setWorkFolder(List<IdentificationDefinition> workFolder) {
        if (ObjectUtil.isAnyNull(workFolder)) {
            throw new ConditionParametersException();
        }

        this.createProcess.setWorkFolder(workFolder);

        return this;
    }

    private void parse() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessHandleTableObject processHandleTable = process.getHandleTable();
        InfoObject info = processHandleTable.get(this.createProcess.getFileHandle());

        //FileContentObject content = info.getContent();
        //content.???

        //...
        this.createProcess.setAppContext(null);
    }

    private void create() {
        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);

        ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

        ProcessEntity process = new ProcessEntity();

        process.setID(UUIDUtil.createRandom());
        process.setStatus(ProcessStatusType.NULL);
        process.setParentProcessID(parentProcess.getID());
        process.setSessionID(UUIDUtil.getEmpty());
        process.setCommunication(ObjectUtil.transferToByteArray(new ProcessCommunicationDefinition()));
        process.setContext(ObjectUtil.transferToByteArray(new ProcessCommunicationDefinition()));
        process.setHandleTable(ObjectUtil.transferToByteArray(new ProcessHandleTableDefinition()));
        process.setStatistics(ObjectUtil.transferToByteArray(new ProcessStatisticsDefinition()));
        process.setToken(ObjectUtil.transferToByteArray(new ProcessTokenDefinition()));

        processRepository.add(process);

        this.process = this.processFactory.buildProcess(process);
    }

    private void configuration() {
        AppContextDefinition appContext = this.createProcess.getAppContext();

        if (ObjectUtil.isAnyNull(appContext)) {
            throw new StatusNotReadyException();
        }

        KernelConfigurationDefinition configuration = this.factoryManager.getKernelSpace().getConfiguration();

        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        ProcessStatusObject processStatus = this.process.getStatus();
        processStatus.initialize();

        ProcessStatisticsObject processStatistics = this.process.getStatistics();
        processStatistics.setDate(DateTimeType.CREATE, nowDateTime);
        processStatistics.setDate(DateTimeType.ACCESS, nowDateTime);

        ProcessTokenObject processToken = this.process.getToken();
        AccountAuthorizationObject accountAuthorization = this.createProcess.getAccountAuthorization();
        if (ObjectUtil.allNotNull(accountAuthorization)) {
            processToken.setAccountAuthorization(accountAuthorization);
        } else {
            processToken.inheritAccountID();

            if (this.createProcess.getPrivilegeTypes() != PrivilegeType.NULL) {
                processToken.inheritPrivileges(this.createProcess.getPrivilegeTypes());
            } else {
                processToken.inheritPrivileges();
            }
            if (ObjectUtil.allNotNull(this.createProcess.getLimits())) {
                processToken.setLimits(this.createProcess.getLimits());
            } else {
                processToken.inheritLimits();
            }
        }

        ProcessHandleTableObject processHandleTable = this.process.getHandleTable();
        processHandleTable.inherit(this.createProcess.getFileHandle());

        ProcessSessionObject processSession = this.process.getSession();
        if (!ValueUtil.isAnyNullOrEmpty(this.createProcess.getSessionID())) {
            processSession.setID(this.createProcess.getSessionID());
        } else {
            processSession.inheritID();
        }

        Set<UUID> roles = new HashSet<>(processToken.getRoles());
        long appContextType = appContext.getType();
        if (appContextType == AppType.SERVICE) {
            roles.add(appContext.getID());
        } else if (appContextType == AppType.BATCH) {
            roles.add(configuration.SECURITY_ROLE_BATCHES_ID);
        } else if (appContextType == AppType.EXECUTABLE) {
            roles.add(configuration.SECURITY_ROLE_EXECUTABLE_ID);
        }
        UserManager userManager = this.factoryManager.getManager(UserManager.class);
        AccountObject account = userManager.getAccount(processToken.getAccountID());
        if (ValueUtil.isAnyNullOrEmpty(account.getPassword())) {
            roles.add(configuration.SECURITY_ROLE_EMPTY_PASSWORD_ID);
        }
        SessionManager sessionManager = this.factoryManager.getManager(SessionManager.class);
        SessionContentObject sessionContent = sessionManager.getAndOpen(processSession.getID());
        long sessionContentType = sessionContent.getType();
        if (LogicalUtil.isAnyEqual(sessionContentType, SessionType.API)) {
            roles.add(configuration.SECURITY_ROLE_API_ID);
        } else if (LogicalUtil.isAnyEqual(sessionContentType, SessionType.GUI)) {
            roles.add(configuration.SECURITY_ROLE_GUI_ID);
        } else if (LogicalUtil.isAnyEqual(sessionContentType, SessionType.CLI)) {
            roles.add(configuration.SECURITY_ROLE_CLI_ID);
        }
        sessionContent.close();
        if (ObjectUtil.allNotNull(this.createProcess.getAdditionalRoles())) {
            roles.addAll(this.createProcess.getAdditionalRoles());
        }
        processToken.setRoles(roles);

        ProcessContextObject processContext = this.process.getContext();
        ProcessContextObject parentProcessContext = this.parentProcess.getContext();

        processContext.setAppContext(appContext);
        if (ObjectUtil.allNotNull(this.createProcess.getEnvironmentVariable())) {
            processContext.setEnvironmentVariable(this.createProcess.getEnvironmentVariable());
        } else {
            processContext.setEnvironmentVariable(this.parentProcess.getContext().getEnvironmentVariable());
        }
        if (ObjectUtil.allNotNull(this.createProcess.getParameters())) {
            processContext.setParameters(this.createProcess.getParameters());
        }
        if (ObjectUtil.allNotNull(this.createProcess.getWorkFolder())) {
            processContext.setWorkFolder(this.createProcess.getWorkFolder());
        } else {
            processContext.setWorkFolder(parentProcessContext.getWorkFolder());
        }

        ProcessCommunicationObject processCommunication = process.getCommunication();
        processCommunication.createSignal(new HashSet<>());

        processStatus.run();
    }

    public ProcessObject build() {
        this.parse();
        this.create();
        this.configuration();

        return this.process;
    }

}
