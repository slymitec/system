package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.exceptions.ConditionParametersException;
import indi.sly.system.common.exceptions.StatusNotSupportedException;
import indi.sly.system.common.utility.ObjectUtils;
import indi.sly.system.common.utility.SpringUtils;
import indi.sly.system.kernel.core.FactoryManager;

import java.lang.reflect.Constructor;

public class CorePrototypeBuilder {
    private FactoryManager factoryManager;

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
            corePrototype = SpringUtils.getInstance(clazz);
        } catch (RuntimeException e) {
            Constructor<T> constructor = null;
            try {
                constructor = clazz.getDeclaredConstructor();
                corePrototype = constructor.newInstance();
            } catch (ReflectiveOperationException e2) {
                try {
                    if (ObjectUtils.allNotNull(constructor) && constructor.trySetAccessible()) {
                        constructor.setAccessible(true);
                        corePrototype = constructor.newInstance();
                    }
                } catch (ReflectiveOperationException e3) {
                    corePrototype = SpringUtils.createInstance(clazz);
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
