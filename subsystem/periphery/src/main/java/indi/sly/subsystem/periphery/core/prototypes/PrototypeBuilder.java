package indi.sly.subsystem.periphery.core.prototypes;

import indi.sly.subsystem.periphery.core.CoreManager;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.lang.reflect.Constructor;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PrototypeBuilder extends ABuilder {
    public final void setFactoryManager(CoreManager coreManager) {
        if (ObjectUtil.allNotNull(coreManager)) {
            this.coreManager = coreManager;
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

        corePrototype.coreManager = this.coreManager;

        return corePrototype;
    }
}
