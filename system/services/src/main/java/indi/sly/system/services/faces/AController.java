package indi.sly.system.services.faces;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.kernel.core.CoreManager;
import indi.sly.system.kernel.core.enviroment.values.KernelSpace;
import indi.sly.system.kernel.core.prototypes.APrototype;

public abstract class AController extends APrototype {
    public final void init() {
        KernelSpace kernelSpace = SpringHelper.getInstance(KernelSpace.class);

        this.coreManager = (CoreManager) kernelSpace.getClassedObjects().getOrDefault(CoreManager.class, null);

        if (ObjectUtil.allNotNull(this.coreManager)) {
            this.coreManager.check();
        }
    }
}
