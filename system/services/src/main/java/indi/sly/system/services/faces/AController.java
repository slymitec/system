package indi.sly.system.services.faces;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.CoreManager;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.prototypes.AObject;
import indi.sly.system.kernel.core.prototypes.APrototype;

import java.util.UUID;

public abstract class AController extends APrototype {
    public final void init() {
        KernelSpaceDefinition kernelSpace = SpringHelper.getInstance(KernelSpaceDefinition.class);

        this.coreManager = (CoreManager) kernelSpace.getClassedObjects().getOrDefault(CoreManager.class, null);
        this.coreManager.check();
    }
}
