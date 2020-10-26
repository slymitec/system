package indi.sly.system.kernel.core;

import indi.sly.system.kernel.core.boot.IStartupRegister;

public abstract class AManager extends ACoreObject implements IStartupRegister {
    @Override
    public void startup(long startupTypes) {
    }

    @Override
    public void shutdown() {
    }
}
