package indi.sly.system.kernel.processes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.ConditionPermissionsException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.date.types.DateTimeTypes;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.prototypes.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.values.ProcessHandleTableDefinition;
import indi.sly.system.kernel.processes.values.ProcessStatisticsDefinition;
import indi.sly.system.kernel.processes.values.ProcessTokenDefinition;
import indi.sly.system.kernel.processes.values.ProcessEntity;
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
    private ProcessFactory processFactory;

    @Override
    public void startup(long startupTypes) {
        if (startupTypes == StartupType.STEP_INIT) {
        } else if (startupTypes == StartupType.STEP_KERNEL) {
            this.processFactory = this.factoryManager.create(ProcessFactory.class);
            this.processFactory.init();
        }
    }

    @Override
    public void shutdown() {
    }

    private ProcessObject getTargetProcess(UUID processID) {
        if (ValueUtil.isAnyNullOrEmpty(processID)) {
            throw new ConditionParametersException();
        }

        MemoryManager memoryManager = this.factoryManager.getManager(MemoryManager.class);
        ProcessRepositoryObject processRepository = memoryManager.getProcessRepository();

        ProcessEntity process = processRepository.get(processID);
        ProcessObject processObject = this.processFactory.buildProcessObject(process);

        DateTimeObject dateTime = this.factoryManager.getCoreRepository().get(SpaceType.KERNEL,
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
        if (ValueUtil.isAnyNullOrEmpty(processID)) {
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
                && !(ObjectUtil.allNotNull(accountAuthorization)
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
        process.setHandleTable(ObjectUtil.transferToByteArray(processHandleTable));
        ProcessStatisticsDefinition processStatistics = new ProcessStatisticsDefinition();
        process.setStatistics(ObjectUtil.transferToByteArray(processStatistics));
        ProcessTokenDefinition processToken = new ProcessTokenDefinition();
        process.setToken(ObjectUtil.transferToByteArray(processToken));


        //...
        return null;
    }


//    public ShadowKernelModeObject shadowKernelMode() {
//        ShadowKernelModeObject shadowKernelMode = this.factoryManager.getCorePrototypeRepository().getByID(SpaceTypes
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
