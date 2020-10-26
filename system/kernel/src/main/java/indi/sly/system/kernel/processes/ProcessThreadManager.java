package indi.sly.system.kernel.processes;

import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.enviroment.SpaceTypes;
import indi.sly.system.kernel.processes.shadows.ShadowKernelModeObject;
import indi.sly.system.kernel.processes.threads.ThreadLifeCycleObject;

public class ProcessThreadManager extends AManager {
    @Override
    public void startup(long startupTypes) {
    }

    @Override
    public void shutdown() {
    }

    public ShadowKernelModeObject shadowKernelMode() {
        ShadowKernelModeObject shadowKernelMode = this.factoryManager.getCoreObjectRepository().getByID(SpaceTypes.KERNEL, ShadowKernelModeObject.class, this.factoryManager.getKernelSpace().getConfiguration().PROCESSES_SHADOW_SHADOWKERNEMODE_ID);

        return shadowKernelMode;
    }

    //Thread Init/Dispose/Do... Object

    public ThreadLifeCycleObject threadLifeCycle() {
        return null;
    }
}
