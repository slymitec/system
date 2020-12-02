package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.types.SpaceTypes;
import indi.sly.system.kernel.core.prototypes.ACoreObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.prototypes.AInfoContentObject;
import indi.sly.system.kernel.objects.prototypes.InfoObject;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.communication.definitions.ProcessCommunicationDefinition;
import indi.sly.system.kernel.processes.definitions.ProcessBuilderDefinition;
import indi.sly.system.kernel.processes.definitions.ProcessHandleTableDefinition;
import indi.sly.system.kernel.processes.definitions.ProcessStatisticsDefinition;
import indi.sly.system.kernel.processes.definitions.ProcessTokenDefinition;
import indi.sly.system.kernel.processes.entities.ProcessEntity;
import indi.sly.system.kernel.processes.types.ProcessStatusTypes;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.types.PrivilegeTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessBuilderObject extends ACoreObject {
    public ProcessBuilderObject() {
        this.processBuilder = new ProcessBuilderDefinition();
    }

    private ProcessObjectBuilderObject processObjectBuilder;
    private ProcessBuilderDefinition processBuilder;
    private ProcessObject process;
    private ProcessObject parentProcess;

    public void setProcessObjectBuilder(ProcessObjectBuilderObject processObjectBuilder) {
        this.processObjectBuilder = processObjectBuilder;
    }

    public void setParentProcess(ProcessObject parentProcess) {
        this.parentProcess = parentProcess;
    }

    //

    public ProcessBuilderObject setFileHandle(UUID fileHandle) {
        if (UUIDUtils.isAnyNullOrEmpty(fileHandle)) {
            throw new ConditionParametersException();
        }

        this.processBuilder.setFileHandle(fileHandle);

        return this;
    }


    public ProcessBuilderObject setAccount(AccountAuthorizationObject accountAuthorization) {
        if (ObjectUtils.isAnyNull(accountAuthorization)) {
            throw new ConditionParametersException();
        }

        this.processBuilder.setAccountAuthorization(accountAuthorization);

        return this;
    }

    //setLimits
    //setRoleTypes

//        processContext.setEnvironmentVariable(null);
//        processContext.setParameters(null);
//        processContext.setSessionID(null);
//        processContext.setWorkFolder(null);

    private void parse() {
        ProcessManager processManager = this.factoryManager.getManager(ProcessManager.class);

        ProcessObject process = processManager.getCurrentProcess();
        ProcessHandleTableObject processHandleTable = process.getHandleTable();
        InfoObject info = processHandleTable.getInfo(this.processBuilder.getFileHandle());

        //FileContentObject content = info.getContent();
        //content.???

        //...
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

        this.process = this.processObjectBuilder.buildProcessObject(process);
    }

    private void configuration() {
        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().get(SpaceTypes.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        ProcessStatusObject processStatus = this.process.getStatus();
        processStatus.initialize();

        ProcessStatisticsObject processStatistics = this.process.getStatistics();
        processStatistics.setDate(DateTimeTypes.CREATE, nowDateTime);
        processStatistics.setDate(DateTimeTypes.ACCESS, nowDateTime);

        ProcessTokenObject processToken = this.process.getToken();
        processToken.inheritAccountID();
        processToken.setAccountID(null);
        processToken.inheritPrivilegeTypes();
        processToken.inheritPrivilegeTypes(PrivilegeTypes.NULL);
        processToken.inheritLimits();
        processToken.setLimits(null);
        processToken.inheritRoleTypes();
        processToken.setRoleTypes(null);

        ProcessHandleTableObject processHandleTable = this.process.getHandleTable();
        processHandleTable.inheritHandle(this.processBuilder.getFileHandle());

        ProcessContextObject processContext = this.process.getContext();
        processContext.setAppContext(null);
        processContext.setEnvironmentVariable(null);
        processContext.setParameters(null);
        processContext.setSessionID(null);
        processContext.setWorkFolder(null);

        //加入 Session 会话列表
    }

    public ProcessObject build() {
        this.parse();
        this.create();
        this.configuration();

        return this.process;
    }
}
