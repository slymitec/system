package indi.sly.system.kernel.core;

import indi.sly.system.common.lang.ConditionContextException;
import indi.sly.system.common.supports.LogicalUtil;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.kernel.core.boot.prototypes.BootFactory;
import indi.sly.system.kernel.core.boot.prototypes.BootObject;
import indi.sly.system.kernel.core.boot.values.StartupType;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.core.prototypes.*;
import indi.sly.system.kernel.core.systemversion.prototypes.SystemVersionObject;
import indi.sly.system.kernel.files.FileSystemManager;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.security.UserManager;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CoreManager extends AManager {
    private CoreFactory factory;

    public CoreFactory getFactory() {
        return this.factory;
    }

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
            this.objectCollection.addByClass(SpaceType.KERNEL, this.create(FileSystemManager.class));
            this.objectCollection.addByClass(SpaceType.KERNEL, this.create(MemoryManager.class));
            this.objectCollection.addByClass(SpaceType.KERNEL, this.create(ObjectManager.class));
            this.objectCollection.addByClass(SpaceType.KERNEL, this.create(ProcessManager.class));
            this.objectCollection.addByClass(SpaceType.KERNEL, this.create(ThreadManager.class));
            this.objectCollection.addByClass(SpaceType.KERNEL, this.create(TypeManager.class));
            this.objectCollection.addByClass(SpaceType.KERNEL, this.create(UserManager.class));

            this.factory = this.coreManager.create(CoreFactory.class);
            this.factory.init();

            BootFactory bootFactory = this.coreManager.create(BootFactory.class);
            bootFactory.init();

            BootObject boot = bootFactory.buildBoot();
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

    public <T extends AService> T getService(Class<T> clazz) {
        T service = this.objectCollection.getByClass(SpaceType.KERNEL, clazz);

        service.check();

        return service;
    }

    public KernelSpaceDefinition getKernelSpace() {
        return SpringHelper.getInstance(KernelSpaceDefinition.class);
    }

    public UserSpaceDefinition getUserSpace() {
        return this.getKernelSpace().getUserSpace();
    }

    public void setUserSpace(UserSpaceDefinition userSpace) {
        this.getKernelSpace().setUserSpace(userSpace);
    }

    public SystemVersionObject getSystemVersion() {
        return this.factory.buildSystemVersion();
    }

    public DateTimeObject getDateTime() {
        return this.factory.buildDateTime();
    }
}
