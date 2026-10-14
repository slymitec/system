package indi.sly.system.kernel.core.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusNotSupportedException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.kernel.core.CoreManager;
import jakarta.inject.Named;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.lang.reflect.Constructor;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PrototypeBuilder extends ABuilder {
    public final void setFactoryManager(CoreManager coreManager) {
        if (ObjectUtil.notNull(coreManager)) {
            this.coreManager = coreManager;
        }
    }

    public <T extends APrototype> T createPrototype(Class<T> clazz, @Nullable Object... args) {
        if (ObjectUtil.isNull(clazz)) {
            throw new ConditionParametersException();
        }

        T corePrototype = null;
        try {
            if (ObjectUtil.isAnyNull(args) || args.length == 0) {
                corePrototype = SpringHelper.getInstance(clazz);
            } else {
                corePrototype = SpringHelper.getInstance(clazz, args);
            }
        } catch (RuntimeException e) {
            Constructor<T> constructor = null;
            try {
                constructor = clazz.getDeclaredConstructor();
                corePrototype = constructor.newInstance(args);
            } catch (ReflectiveOperationException e2) {
                try {
                    if (ObjectUtil.notNull(constructor) && constructor.trySetAccessible()) {
                        constructor.setAccessible(true);
                        corePrototype = constructor.newInstance(args);
                    }
                } catch (ReflectiveOperationException e3) {
                    corePrototype = SpringHelper.createInstance(clazz);
                }
            }
        }

        if (ObjectUtil.isNull(corePrototype)) {
            throw new StatusNotSupportedException();
        }

        corePrototype.coreManager = this.coreManager;

        return corePrototype;
    }
}
