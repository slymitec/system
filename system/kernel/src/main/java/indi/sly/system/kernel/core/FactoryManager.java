package indi.sly.system.kernel.core;

import indi.sly.system.common.exceptions.AKernelException;
import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.StatusNotSupportedException;
import indi.sly.system.common.exceptions.StatusRelationshipErrorException;
import indi.sly.system.common.functions.Provider;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.SpringUtils;
import indi.sly.system.kernel.core.boot.types.StartupTypes;
import indi.sly.system.kernel.core.date.prototypes.DateTimeObject;
import indi.sly.system.kernel.core.enviroment.KernelSpace;
import indi.sly.system.kernel.core.enviroment.types.SpaceTypes;
import indi.sly.system.kernel.core.enviroment.UserSpace;
import indi.sly.system.kernel.core.prototypes.ACorePrototype;
import indi.sly.system.kernel.core.prototypes.CoreRepositoryObject;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.TypeManager;
import indi.sly.system.kernel.processes.ProcessManager;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.security.SecurityTokenManager;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.objenesis.SpringObjenesis;

import javax.inject.Named;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FactoryManager extends AManager {
    @Override
    public void startup(long startupTypes) {
        if (startupTypes == StartupTypes.STEP_INIT) {
            this.factoryManager = this;

            UserSpace bootUserSpace = new UserSpace();
            this.factoryManager.setUserSpaceContainer(() -> {
                return bootUserSpace;
            });

            this.coreRepository = this.create(CoreRepositoryObject.class);

            this.coreRepository.add(SpaceTypes.KERNEL, this.create(FactoryManager.class));
            this.coreRepository.add(SpaceTypes.KERNEL, this.create(MemoryManager.class));
            this.coreRepository.add(SpaceTypes.KERNEL, this.create(ProcessManager.class));
            this.coreRepository.add(SpaceTypes.KERNEL, this.create(ThreadManager.class));
            this.coreRepository.add(SpaceTypes.KERNEL, this.create(TypeManager.class));
            this.coreRepository.add(SpaceTypes.KERNEL, this.create(ObjectManager.class));
            this.coreRepository.add(SpaceTypes.KERNEL, this.create(SecurityTokenManager.class));
            // ...
            this.coreRepository.add(SpaceTypes.KERNEL, this.create(DateTimeObject.class));
            this.coreRepository.add(SpaceTypes.KERNEL, this.create(CoreRepositoryObject.class));
        } else if (startupTypes == StartupTypes.STEP_KERNEL) {
        }
    }

    private CoreRepositoryObject coreRepository;

    public <T extends ACorePrototype> T create(Class<T> clazz) {
        if (ObjectUtils.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        T corePrototype = null;
        try {
            corePrototype = SpringUtils.getApplicationContext().getBean(clazz);
        } catch (AKernelException e) {
            Constructor<T> constructor = null;
            try {
                constructor = clazz.getDeclaredConstructor();
                corePrototype = constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e2) {
                try {
                    if (ObjectUtils.allNotNull(constructor) && constructor.trySetAccessible()) {
                        constructor.setAccessible(true);
                        corePrototype = constructor.newInstance();
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e3) {
                    corePrototype = new SpringObjenesis().newInstance(clazz);
                }
            }
        }

        if (ObjectUtils.isAnyNull(corePrototype)) {
            throw new StatusNotSupportedException();
        }

        corePrototype.setFactoryManager(this);

        return corePrototype;
    }

    public CoreRepositoryObject getCoreRepository() {
        return this.coreRepository;
    }

    @SuppressWarnings("unchecked")
    public <T extends AManager> T getManager(Class<T> clazz) {
        T corePrototype = this.coreRepository.get(SpaceTypes.KERNEL, clazz);
        if (!(corePrototype instanceof AManager)) {
            throw new StatusRelationshipErrorException();
        }
        return corePrototype;
    }

    public KernelSpace getKernelSpace() {
        return SpringUtils.getApplicationContext().getBean(KernelSpace.class);
    }

    private Provider<UserSpace> userSpaceContainer;

    public UserSpace getUserSpace() {
        return this.userSpaceContainer == null ? null : this.userSpaceContainer.acquire();
    }

    public void setUserSpaceContainer(Provider<UserSpace> userSpaceContainer) {
        this.userSpaceContainer = userSpaceContainer;
    }
}
