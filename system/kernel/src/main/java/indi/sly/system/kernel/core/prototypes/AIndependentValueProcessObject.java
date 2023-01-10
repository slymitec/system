package indi.sly.system.kernel.core.prototypes;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AIndependentValueProcessObject<T> extends AIndependentProcessObject<T> {
    protected T value;

    protected void read(T source) {
        this.value = source;
    }

    protected T write() {
        return this.value;
    }
}
