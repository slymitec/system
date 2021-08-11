package indi.sly.system.kernel.core.prototypes;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AValueProcessObject<T> extends ACoreProcessObject<T> {
    protected T value;

    protected void read(T source) {
        this.value = source;
    }

    protected T write() {
        return this.value;
    }
}
