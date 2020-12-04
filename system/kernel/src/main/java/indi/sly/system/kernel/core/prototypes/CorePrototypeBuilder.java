package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.exceptions.AKernelException;
import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.StatusNotSupportedException;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.SpringUtils;
import indi.sly.system.kernel.core.FactoryManager;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.objenesis.SpringObjenesis;

import javax.inject.Named;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class CorePrototypeBuilder extends ACorePrototype {
    public final void setFactoryManager(FactoryManager factoryManager) {
        if (ObjectUtils.allNotNull(this.factoryManager)) {
            this.factoryManager = factoryManager;
        }
    }

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

        corePrototype.factoryManager = this.factoryManager;

        return corePrototype;
    }
}
