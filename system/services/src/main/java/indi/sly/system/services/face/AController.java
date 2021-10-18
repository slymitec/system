package indi.sly.system.services.face;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.FactoryManager;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.core.values.HandleEntryDefinition;

import java.util.UUID;

public abstract class AController extends APrototype {
    public final void init() {
        KernelSpaceDefinition kernelSpace = SpringHelper.getInstance(KernelSpaceDefinition.class);

        HandleEntryDefinition factoryManagerHandleEntry = kernelSpace.getClassedHandles().getOrDefault(FactoryManager.class, null);
        if (ObjectUtil.isAnyNull(factoryManagerHandleEntry)) {
            return;
        }
        UUID factoryManagerID = factoryManagerHandleEntry.getID();
        if (ValueUtil.isAnyNullOrEmpty(factoryManagerID)) {
            return;
        }
        this.factoryManager = (FactoryManager) kernelSpace.getCoreObjects().getOrDefault(factoryManagerID, null);
        this.factoryManager.check();
    }
}
