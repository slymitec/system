package indi.sly.subsystem.periphery.core;

import indi.sly.subsystem.periphery.calls.CallManager;
import indi.sly.subsystem.periphery.core.boot.prototypes.BootFactory;
import indi.sly.subsystem.periphery.core.boot.prototypes.BootObject;
import indi.sly.subsystem.periphery.core.boot.values.StartupType;
import indi.sly.subsystem.periphery.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.subsystem.periphery.core.enviroment.values.SpaceType;
import indi.sly.subsystem.periphery.core.enviroment.values.UserSpaceDefinition;
import indi.sly.subsystem.periphery.core.prototypes.APrototype;
import indi.sly.subsystem.periphery.core.prototypes.ObjectCollectionObject;
import indi.sly.subsystem.periphery.core.prototypes.PrototypeBuilder;
import indi.sly.subsystem.periphery.proxies.ProxyManager;
import indi.sly.system.common.lang.ConditionContextException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CoreManager extends AManager {
    protected BootFactory bootFactory;

    @Override
    public void startup(long startup) {
        if (LogicalUtil.isAnyEqual(startup, StartupType.STEP_INIT_SELF)) {
            this.coreManager = this;
            this.coreManager.check();

            this.prototypeBuilder = SpringHelper.getInstance(PrototypeBuilder.class);
            this.prototypeBuilder.setFactoryManager(this);

            this.objectCollection = this.coreManager.create(ObjectCollectionObject.class);
            this.objectCollection.setLimit(SpaceType.KERNEL, Long.MAX_VALUE);
            this.objectCollection.addByClass(SpaceType.KERNEL, this);
            this.objectCollection.addByClass(SpaceType.KERNEL, this.create(CallManager.class));
            this.objectCollection.addByClass(SpaceType.KERNEL, this.create(ProxyManager.class));

            this.bootFactory = this.coreManager.create(BootFactory.class);
            this.bootFactory.init();

            BootObject boot = this.bootFactory.buildBoot();
            this.objectCollection.addByClass(SpaceType.KERNEL, boot);
        }
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void check() {
        if (ObjectUtil.isAnyNull(this.coreManager)) {
            throw new ConditionContextException();
        }
    }

    private PrototypeBuilder prototypeBuilder;
    private ObjectCollectionObject objectCollection;

    public ObjectCollectionObject getObjectCollection() {
        return this.objectCollection;
    }

    public <T extends APrototype> T create(Class<T> clazz) {
        return this.prototypeBuilder.createPrototype(clazz);
    }

    public <T extends AManager> T getManager(Class<T> clazz) {
        T manager = this.objectCollection.getByClass(SpaceType.KERNEL, clazz);

        manager.check();

        return manager;
    }

    public KernelSpaceDefinition getKernelSpace() {
        return SpringHelper.getInstance(KernelSpaceDefinition.class);
    }

    public UserSpaceDefinition getUserSpace() {
        return this.getKernelSpace().getUserSpace();
    }
}
