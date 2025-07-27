package indi.sly.subsystem.periphery.core.prototypes.processors;

import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AResolver extends AProcessor implements IOrderlyResolver {
    @Override
    public int order() {
        return 0;
    }

    @Override
    public final int compareTo(IOrderlyResolver other) {
        return this.order() - other.order();
    }
}
