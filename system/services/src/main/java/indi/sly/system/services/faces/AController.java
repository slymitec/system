package indi.sly.system.services.faces;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.CoreManager;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.prototypes.APrototype;

import java.util.UUID;

public abstract class AController extends APrototype {
    public final void init() {
        KernelSpaceDefinition kernelSpace = SpringHelper.getInstance(KernelSpaceDefinition.class);

        HandleEntryDefinition factoryManagerHandleEntry = kernelSpace.getClassedHandles().getOrDefault(CoreManager.class, null);
        if (ObjectUtil.isAnyNull(factoryManagerHandleEntry)) {
            return;
        }
        UUID factoryManagerID = factoryManagerHandleEntry.getID();
        if (ValueUtil.isAnyNullOrEmpty(factoryManagerID)) {
            return;
        }
        this.coreManager = (CoreManager) kernelSpace.getCoreObjects().getOrDefault(factoryManagerID, null);
        this.coreManager.check();
    }
}
