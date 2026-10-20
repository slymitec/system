package indi.sly.subsystem.applications.framework.controllers;

import indi.sly.subsystem.periphery.core.CoreManager;
import indi.sly.subsystem.periphery.core.environment.containers.KernelSpace;
import indi.sly.subsystem.periphery.core.prototypes.APrototype;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import jakarta.annotation.PostConstruct;

public abstract class AController extends APrototype {
    @PostConstruct
    public void init() {
        if (ObjectUtil.isNull(this.coreManager)) {
            KernelSpace kernelSpace = SpringHelper.getInstance(KernelSpace.class);

            this.coreManager = (CoreManager) kernelSpace.getClassedObjects().get(CoreManager.class);

            if (ObjectUtil.notNull(this.coreManager)) {
                this.coreManager.check();
            }
        }
    }
}
