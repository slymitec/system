package indi.sly.system.kernel.core;

import indi.sly.system.kernel.core.boot.IStartupRegister;
import indi.sly.system.kernel.core.prototypes.ACoreObject;

public abstract class AManager extends ACoreObject implements IStartupRegister {
    @Override
    public void startup(long startupTypes) {
    }

    @Override
    public void shutdown() {
    }
}
