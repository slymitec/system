package indi.sly.clisubsystem.periphery.core.prototypes;

import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

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
