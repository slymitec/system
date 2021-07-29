package indi.sly.system.services.core;

import indi.sly.system.kernel.core.boot.prototypes.IStartupCapable;
import indi.sly.system.kernel.core.prototypes.APrototype;

public abstract class AService extends APrototype implements IStartupCapable {
    @Override
    public void startup(long startup) {
    }

    @Override
    public void shutdown() {
    }

    public void check() {
    }
}
