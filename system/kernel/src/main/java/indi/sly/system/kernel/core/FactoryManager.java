package indi.sly.system.kernel.core;

import indi.sly.system.common.exceptions.AKernelException;
import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.StatusRelationshipErrorException;
import indi.sly.system.common.functions.Provider;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.kernel.core.boot.StartupTypes;
import indi.sly.system.kernel.core.date.DateTimeObject;
import indi.sly.system.kernel.core.enviroment.KernelSpace;
import indi.sly.system.kernel.core.enviroment.SpaceTypes;
import indi.sly.system.kernel.core.enviroment.UserSpace;
import indi.sly.system.kernel.memory.MemoryManager;
import indi.sly.system.kernel.objects.ObjectManager;
import indi.sly.system.kernel.objects.TypeManager;
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
            this.coreObjectRepository = ObjectUtils.getObject(CoreObjectRepositoryObject.class);

            this.coreObjectRepository.add(SpaceTypes.KERNEL, ObjectUtils.getObject(FactoryManager.class));
            this.coreObjectRepository.add(SpaceTypes.KERNEL, ObjectUtils.getObject(MemoryManager.class));
            this.coreObjectRepository.add(SpaceTypes.KERNEL, ObjectUtils.getObject(TypeManager.class));
            this.coreObjectRepository.add(SpaceTypes.KERNEL, ObjectUtils.getObject(ObjectManager.class));
            // ...
            this.coreObjectRepository.add(SpaceTypes.KERNEL, ObjectUtils.getObject(DateTimeObject.class));
            this.coreObjectRepository.add(SpaceTypes.KERNEL, ObjectUtils.getObject(CoreObjectRepositoryObject.class));
        } else if (startupTypes == StartupTypes.STEP_KERNEL) {
        }
    }

    private CoreObjectRepositoryObject coreObjectRepository;

    public <T extends ACoreObject> T create(Class<T> clazz) {
        if (ObjectUtils.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        T coreObject = null;
        try {
            coreObject = ObjectUtils.getObject(clazz);
        } catch (AKernelException e) {
            Constructor<T> constructor = null;
            try {
                constructor = clazz.getDeclaredConstructor();
                coreObject = constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e2) {
                try {
                    if (ObjectUtils.allNotNull(constructor) && constructor.trySetAccessible()) {
                        constructor.setAccessible(true);
                        coreObject = constructor.newInstance();
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e3) {
                }
            }
        }
        if (ObjectUtils.isAnyNull(coreObject)) {
            coreObject = new SpringObjenesis().newInstance(clazz);
        }

        coreObject.factoryManager = this;

        return coreObject;
    }

    public CoreObjectRepositoryObject getCoreObjectRepository() {
        return this.coreObjectRepository;
    }

    @SuppressWarnings("unchecked")
    public <T extends AManager> T getManager(Class<T> clazz) {
        T coreObject = this.coreObjectRepository.get(SpaceTypes.KERNEL, clazz);
        if (!(coreObject instanceof AManager)) {
            throw new StatusRelationshipErrorException();
        }
        return coreObject;
    }

    public KernelSpace getKernelSpace() {
        return ObjectUtils.getObject(KernelSpace.class);
    }

    private Provider<UserSpace> userSpaceContainer;

    public UserSpace getUserSpace() {
        return this.userSpaceContainer == null ? null : this.userSpaceContainer.acquire();
    }

    public void setUserSpaceContainer(Provider<UserSpace> userSpaceContainer) {
        this.userSpaceContainer = userSpaceContainer;
    }
}
