package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.common.values.IdentificationDefinition;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.communication.values.ProcessCommunicationDefinition;
import indi.sly.system.kernel.processes.communication.prototypes.ProcessCommunicationObject;
import indi.sly.system.kernel.processes.sessions.prototypes.ProcessSessionObject;
import indi.sly.system.kernel.processes.values.CreateProcessDefinition;
import indi.sly.system.kernel.processes.values.ProcessHandleTableDefinition;
import indi.sly.system.kernel.processes.values.ProcessStatisticsDefinition;
import indi.sly.system.kernel.processes.values.ProcessTokenDefinition;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.values.ProcessStatusType;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.types.PrivilegeTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreateProcessBuilder extends APrototype {
    public CreateProcessBuilder() {
        this.createProcess = new CreateProcessDefinition();
    }

    private ProcessFactory processFactory;
    private ProcessObject parentProcess;
    private final CreateProcessDefinition createProcess;
    private ProcessObject process;

    public void setProcessFactory(ProcessFactory processFactory) {
        this.processFactory = processFactory;
    }

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
        if (!parentProcessToken.isPrivilegeType(PrivilegeTypes.PROCESSES_MODIFY_LIMITS)) {
            throw new ConditionPermissionsException();
        }

        this.createProcess.setLimits(limits);

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
        if (!parentProcessToken.isPrivilegeType(PrivilegeTypes.CORE_MODIFY_PRIVILEGES)) {
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
        if (!parentProcessToken.isPrivilegeType(PrivilegeTypes.SESSION_MODIFY_USERSESSION)) {
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
        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        ProcessStatusObject processStatus = this.process.getStatus();
        processStatus.initialize();

        ProcessStatisticsObject processStatistics = this.process.getStatistics();
        processStatistics.setDate(DateTimeTypes.CREATE, nowDateTime);
        processStatistics.setDate(DateTimeTypes.ACCESS, nowDateTime);

        ProcessTokenObject processToken = this.process.getToken();
        AccountAuthorizationObject accountAuthorization = this.createProcess.getAccountAuthorization();
        if (ObjectUtil.allNotNull(accountAuthorization)) {
            processToken.setAccountAuthorization(accountAuthorization);
        } else {
            processToken.inheritAccountID();

            if (this.createProcess.getPrivilegeTypes() != PrivilegeTypes.NULL) {
                processToken.inheritPrivilegeTypes(this.createProcess.getPrivilegeTypes());
            } else {
                processToken.inheritPrivilegeTypes();
            }
            if (ObjectUtil.allNotNull(this.createProcess.getLimits())) {
                processToken.setLimits(this.createProcess.getLimits());
            } else {
                processToken.inheritLimits();
            }
        }
        if (ObjectUtil.isAnyNull(this.createProcess.getAppContext().getRoles())) {
            processToken.inheritRoleTypes();
        } else {
            processToken.setRoleTypes(this.createProcess.getAppContext().getRoles());
        }

        ProcessHandleTableObject processHandleTable = this.process.getHandleTable();
        processHandleTable.inherit(this.createProcess.getFileHandle());

        ProcessSessionObject processSession = this.process.getSession();
        if (!ValueUtil.isAnyNullOrEmpty(this.createProcess.getSessionID())) {
            processSession.setID(this.createProcess.getSessionID());
        } else {
            processSession.inheritID();
        }

        ProcessContextObject processContext = this.process.getContext();
        ProcessContextObject parentProcessContext = this.parentProcess.getContext();

        if (ObjectUtil.allNotNull(this.createProcess.getAppContext())) {
            processContext.setAppContext(this.createProcess.getAppContext());
        }
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
