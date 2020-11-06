package indi.sly.system.kernel.processes;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.ConditionPermissionsException;
import indi.sly.system.common.utility.UUIDUtils;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.boot.StartupTypes;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.memory.repositories.ProcessRepositoryObject;
import indi.sly.system.kernel.processes.entities.ProcessEntity;
import indi.sly.system.kernel.processes.prototypes.ProcessObject;
import indi.sly.system.kernel.processes.prototypes.ProcessObjectFactoryObject;
import indi.sly.system.kernel.processes.prototypes.ProcessTokenObject;
import indi.sly.system.kernel.security.prototypes.PrivilegeTypes;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessManager extends AManager {
    private ProcessObjectFactoryObject processObjectfactory;

    @Override
    public void startup(long startupTypes) {
        if (startupTypes == StartupTypes.STEP_INIT) {

        } else if (startupTypes == StartupTypes.STEP_KERNEL) {
            this.processObjectfactory = this.factoryManager.create(ProcessObjectFactoryObject.class);
            this.processObjectfactory.initProcessObjectFactory();
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

        return this.processObjectfactory.buildProcessObject(process);
    }


    public ProcessObject getCurrentProcess() {
        //Thread...
        UUID processID = UUIDUtils.createRandom();

        return this.getTargetProcess(processID);
    }

    public ProcessObject getProcess(UUID processID) {
        ProcessObject currentProcess = this.getCurrentProcess();
        ProcessTokenObject currentProcessToken = currentProcess.getToken();
        ProcessObject process = this.getTargetProcess(processID);
        ProcessTokenObject processToken = process.getToken();

        if (!currentProcessToken.getAccountID().equals(processToken.getAccountID()) && !currentProcessToken.isPrivilegeTypes(PrivilegeTypes.PROCESSES_RUN_APP_WITH_ANOTHER_ACCOUNT)) {
            throw new ConditionPermissionsException();
        }

        return process;
    }

    public ProcessObject createProcess() {
        //...
        return null;
    }


//    public ShadowKernelModeObject shadowKernelMode() {
//        ShadowKernelModeObject shadowKernelMode = this.factoryManager.getCoreObjectRepository().getByID(SpaceTypes.KERNEL, ShadowKernelModeObject.class, this.factoryManager.getKernelSpace().getConfiguration().PROCESSES_SHADOW_SHADOWKERNEMODE_ID);
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
