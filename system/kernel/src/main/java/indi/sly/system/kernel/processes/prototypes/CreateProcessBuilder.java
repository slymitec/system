package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.types.SpaceTypes;
import indi.sly.system.kernel.core.prototypes.ACorePrototype;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.objects.Identification;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.communication.values.ProcessCommunicationDefinition;
import indi.sly.system.kernel.processes.communication.prototypes.ProcessCommunicationObject;
import indi.sly.system.kernel.processes.values.CreateProcessDefinition;
import indi.sly.system.kernel.processes.values.ProcessHandleTableDefinition;
import indi.sly.system.kernel.processes.values.ProcessStatisticsDefinition;
import indi.sly.system.kernel.processes.values.ProcessTokenDefinition;
import indi.sly.system.kernel.processes.values.ProcessEntity;
import indi.sly.system.kernel.processes.types.ProcessStatusTypes;
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
public class CreateProcessBuilder extends ACorePrototype {
    public CreateProcessBuilder() {
        this.createProcess = new CreateProcessDefinition();
    }

    private ProcessFactory processFactory;
    private CreateProcessDefinition createProcess;
    private ProcessObject process;
    private ProcessObject parentProcess;

    public void setProcessObjectBuilder(ProcessFactory processFactory) {
        this.processFactory = processFactory;
    }

    public void setParentProcess(ProcessObject parentProcess) {
        this.parentProcess = parentProcess;
    }

    //

    public CreateProcessBuilder setFileHandle(UUID fileHandle) {
        if (UUIDUtils.isAnyNullOrEmpty(fileHandle)) {
            throw new ConditionParametersException();
        }

        this.createProcess.setFileHandle(fileHandle);

        return this;
    }


    public CreateProcessBuilder setAccountAuthorization(AccountAuthorizationObject accountAuthorization) {
        if (ObjectUtils.isAnyNull(accountAuthorization)) {
            throw new ConditionParametersException();
        }

        this.createProcess.setAccountAuthorization(accountAuthorization);

        return this;
    }

    public CreateProcessBuilder setPrivilegeTypes(long privilegeTypes) {
        ProcessTokenObject parentProcessToken = this.parentProcess.getToken();
        if (!parentProcessToken.isPrivilegeTypes(PrivilegeTypes.CORE_MODIFY_PRIVILEGES)) {
            throw new ConditionPermissionsException();
        }

        this.createProcess.setPrivilegeTypes(privilegeTypes);

        return this;
    }

    public CreateProcessBuilder setLimits(Map<Long, Integer> limits) {
        if (ObjectUtils.isAnyNull(limits)) {
            throw new ConditionParametersException();
        }

        ProcessTokenObject parentProcessToken = this.parentProcess.getToken();
        if (!parentProcessToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_MODIFY_LIMITS)) {
            throw new ConditionPermissionsException();
        }

        this.createProcess.setLimits(limits);

        return this;
    }

    public CreateProcessBuilder setEnvironmentVariable(Map<String, String> environmentVariable) {
        this.createProcess.setEnvironmentVariable(environmentVariable);

        return this;
    }

    public CreateProcessBuilder setParameters(Map<String, String> parameters) {
        this.createProcess.setParameters(parameters);

        return this;
    }

    public CreateProcessBuilder setWorkFolder(List<Identification> workFolder) {
        this.createProcess.setWorkFolder(workFolder);

        return this;
    }

//        processContext.setSessionID(null);

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

        process.setID(UUIDUtils.createRandom());
        process.setStatus(ProcessStatusTypes.NULL);
        process.setParentProcessID(parentProcess.getID());
        process.setSessionID(UUIDUtils.getEmpty());
        process.setCommunication(ObjectUtils.transferToByteArray(new ProcessCommunicationDefinition()));
        process.setContext(ObjectUtils.transferToByteArray(new ProcessCommunicationDefinition()));
        process.setHandleTable(ObjectUtils.transferToByteArray(new ProcessHandleTableDefinition()));
        process.setStatistics(ObjectUtils.transferToByteArray(new ProcessStatisticsDefinition()));
        process.setToken(ObjectUtils.transferToByteArray(new ProcessTokenDefinition()));

        processRepository.add(process);

        this.process = this.processFactory.buildProcessObject(process);
    }

    private void configuration() {
        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceTypes.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        ProcessStatusObject processStatus = this.process.getStatus();
        processStatus.initialize();

        ProcessStatisticsObject processStatistics = this.process.getStatistics();
        processStatistics.setDate(DateTimeTypes.CREATE, nowDateTime);
        processStatistics.setDate(DateTimeTypes.ACCESS, nowDateTime);

        ProcessTokenObject processToken = this.process.getToken();
        AccountAuthorizationObject accountAuthorization = this.createProcess.getAccountAuthorization();
        if (ObjectUtils.allNotNull(accountAuthorization)) {
            processToken.setAccountAuthorization(accountAuthorization);
        } else {
            processToken.inheritAccountID();

            if (this.createProcess.getPrivilegeTypes() != PrivilegeTypes.NULL) {
                processToken.inheritPrivilegeTypes(this.createProcess.getPrivilegeTypes());
            } else {
                processToken.inheritPrivilegeTypes();
            }
            if (ObjectUtils.allNotNull(this.createProcess.getLimits())) {
                processToken.setLimits(this.createProcess.getLimits());
            } else {
                processToken.inheritLimits();
            }
        }
        if (ObjectUtils.isAnyNull(this.createProcess.getAppContext().getRoles())) {
            processToken.inheritRoleTypes();
        } else {
            processToken.setRoleTypes(this.createProcess.getAppContext().getRoles());
        }

        ProcessHandleTableObject processHandleTable = this.process.getHandleTable();
        processHandleTable.inherit(this.createProcess.getFileHandle());

        ProcessContextObject processContext = this.process.getContext();
        ProcessContextObject parentProcessContext = this.parentProcess.getContext();

        if (ObjectUtils.allNotNull(this.createProcess.getAppContext())) {
            processContext.setAppContext(this.createProcess.getAppContext());
        }
        if (ObjectUtils.allNotNull(this.createProcess.getEnvironmentVariable())) {
            processContext.setEnvironmentVariable(this.createProcess.getEnvironmentVariable());
        } else {
            processContext.setEnvironmentVariable(this.parentProcess.getContext().getEnvironmentVariable());
        }
        if (ObjectUtils.allNotNull(this.createProcess.getParameters())) {
            processContext.setParameters(this.createProcess.getParameters());
        }
        if (!UUIDUtils.isAnyNullOrEmpty(this.createProcess.getSessionID())) {
            processContext.setSessionID(this.createProcess.getSessionID());
        } else {
            UUID parentProcessContextSessionID = parentProcessContext.getSessionID();
            if (!UUIDUtils.isAnyNullOrEmpty(parentProcessContextSessionID)) {
                processContext.setSessionID(parentProcessContextSessionID);
            }
        }
        if (ObjectUtils.allNotNull(this.createProcess.getWorkFolder())) {
            processContext.setWorkFolder(this.createProcess.getWorkFolder());
        } else {
            processContext.setWorkFolder(parentProcessContext.getWorkFolder());
        }

        //加入 Session 会话列表
    }

    public ProcessObject build() {
        this.parse();
        this.create();
        this.configuration();

        return this.process;
    }

    public void initialize() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();

        ProcessCommunicationObject processCommunication = process.getCommunication();
        processCommunication.createSignal(new HashSet<>());

        ProcessStatusObject processStatus = process.getStatus();
        processStatus.run();
    }
}
