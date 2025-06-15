package indi.sly.system.kernel.core.prototypes.processors;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

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
