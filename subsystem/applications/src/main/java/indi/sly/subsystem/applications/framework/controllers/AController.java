package indi.sly.subsystem.applications.framework.controllers;

import indi.sly.subsystem.periphery.core.CoreManager;
import indi.sly.subsystem.periphery.core.prototypes.APrototype;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import jakarta.annotation.PostConstruct;

public abstract class AController extends APrototype {
    @PostConstruct
    public void init() {
        if (ObjectUtil.isNull(this.coreManager)) {
            this.coreManager = SpringHelper.getInstance(CoreManager.class);
        }
    }
}
