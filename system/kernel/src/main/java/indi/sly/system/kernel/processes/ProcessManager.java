package indi.sly.system.kernel.processes;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.types.StartupTypes;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.types.SpaceTypes;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.definitions.ProcessHandleTableDefinition;
import indi.sly.system.kernel.processes.definitions.ProcessStatisticsDefinition;
import indi.sly.system.kernel.processes.definitions.ProcessTokenDefinition;
import indi.sly.system.kernel.processes.entities.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.*;
import indi.sly.system.kernel.security.prototypes.AccountAuthorizationObject;
import indi.sly.system.kernel.security.types.PrivilegeTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessManager extends AManager {
    private ProcessObjectBuilderObject processObjectFactory;

    @Override
    public void startup(long startupTypes) {
        if (startupTypes == StartupTypes.STEP_INIT) {
        } else if (startupTypes == StartupTypes.STEP_KERNEL) {
            this.processObjectFactory = this.factoryManager.create(ProcessObjectBuilderObject.class);
            this.processObjectFactory.initProcessObjectFactory();
        }
    }

    @Override
    public void shutdown() {
    }

    private ProcessObject getTargetProcess(UUID processID) {
        if (UUIDUtils.isAnyNullOrEmpty(processID)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

        ProcessEntity process = processRepository.get(processID);
        ProcessObject processObject = this.processObjectFactory.buildProcessObject(process);

        DateTimeObject dateTime = this.factoryManager.getCoreObjectRepository().get(SpaceTypes.KERNEL,
                DateTimeObject.class);
        long nowDateTime = dateTime.getCurrentDateTime();

        ProcessStatisticsObject processStatistics = processObject.getStatistics();
        processStatistics.setDate(DateTimeTypes.ACCESS, nowDateTime);

        return processObject;
    }


    public ProcessObject getCurrentProcess() {
        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

        ThreadObject thread = threadManager.getCurrentThread();

        return this.getTargetProcess(thread.getProcessID());
    }

    public ProcessObject getProcess(UUID processID) {
        return this.getProcess(processID, null);
    }

    public ProcessObject getProcess(UUID processID, AccountAuthorizationObject accountAuthorization) {
        if (UUIDUtils.isAnyNullOrEmpty(processID)) {
            throw new ConditionParametersException();
        }

        ProcessObject currentProcess = this.getCurrentProcess();
        if (currentProcess.getID().equals(processID)) {
            return currentProcess;
        }
        ProcessTokenObject currentProcessToken = currentProcess.getToken();

        ProcessObject process = this.getTargetProcess(processID);
        ProcessTokenObject processToken = process.getToken();

        if (!currentProcessToken.getAccountID().equals(processToken.getAccountID())
                && (!currentProcessToken.isPrivilegeTypes(PrivilegeTypes.SECURITY_DO_WITH_ANY_ACCOUNT)
                && !(ObjectUtils.allNotNull(accountAuthorization)
                && accountAuthorization.checkAndGetResult().getAccountID().equals(processToken.getAccountID())))) {
            throw new ConditionPermissionsException();
        }

        return process;
    }

    public ProcessObject createProcess() {
        ProcessObject currentProcess = this.getCurrentProcess();

        ProcessEntity process = new ProcessEntity();
        process.setID(UUID.randomUUID());
        process.setParentProcessID(currentProcess.getID());
        process.setSessionID(currentProcess.getSessionID());
        ProcessHandleTableDefinition processHandleTable = new ProcessHandleTableDefinition();
        process.setHandleTable(ObjectUtils.transferToByteArray(processHandleTable));
        ProcessStatisticsDefinition processStatistics = new ProcessStatisticsDefinition();
        process.setStatistics(ObjectUtils.transferToByteArray(processStatistics));
        ProcessTokenDefinition processToken = new ProcessTokenDefinition();
        process.setToken(ObjectUtils.transferToByteArray(processToken));


        //...
        return null;
    }


//    public ShadowKernelModeObject shadowKernelMode() {
//        ShadowKernelModeObject shadowKernelMode = this.factoryManager.getCoreObjectRepository().getByID(SpaceTypes
//        .KERNEL, ShadowKernelModeObject.class, this.factoryManager.getKernelSpace().getConfiguration()
//        .PROCESSES_SHADOW_SHADOWKERNEMODE_ID);
//
//        return shadowKernelMode;
//    }
//
//    //Thread Init/Dispose/Do... Object
//
//    public ThreadLifeCycleObject threadLifeCycle() {
//        return null;
//    }
}
