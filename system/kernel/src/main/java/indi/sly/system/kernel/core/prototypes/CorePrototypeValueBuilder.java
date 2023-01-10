package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.kernel.core.FactoryManager;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.lang.reflect.Constructor;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CorePrototypeValueBuilder extends ABuilder {
    public final void setFactoryManager(FactoryManager factoryManager) {
        if (ObjectUtil.allNotNull(factoryManager)) {
            this.factoryManager = factoryManager;
        }
    }

    public <T extends APrototype> T createPrototype(Class<T> clazz) {
        if (ObjectUtil.isAnyNull(clazz)) {
            throw new ConditionParametersException();
        }

        T corePrototype = null;
        try {
            corePrototype = SpringHelper.getInstance(clazz);
        } catch (RuntimeException e) {
            Constructor<T> constructor = null;
            try {
                constructor = clazz.getDeclaredConstructor();
                corePrototype = constructor.newInstance();
            } catch (ReflectiveOperationException e2) {
                try {
                    if (ObjectUtil.allNotNull(constructor) && constructor.trySetAccessible()) {
                        constructor.setAccessible(true);
                        corePrototype = constructor.newInstance();
                    }
                } catch (ReflectiveOperationException e3) {
                    corePrototype = SpringHelper.createInstance(clazz);
                }
            }
        }

        if (ObjectUtil.isAnyNull(corePrototype)) {
            throw new StatusNotSupportedException();
        }

        corePrototype.factoryManager = this.factoryManager;

        return corePrototype;
    }
}
