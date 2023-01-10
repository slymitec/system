package indi.sly.system.kernel.core.prototypes;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AValueProcessObject<T1, T2> extends AProcessObject<T1, T2> {
    protected T1 value;

    protected void read(T1 source) {
        this.value = source;
    }

    protected T1 write() {
        return this.value;
    }
}
