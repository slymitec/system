package indi.sly.system.kernel.core.boot.prototypes;

import indi.sly.system.kernel.core.boot.lang.BootStartConsumer;
import indi.sly.system.kernel.core.boot.prototypes.wrappers.BootProcessorMediator;
import indi.sly.system.kernel.core.prototypes.AObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BootObject extends AObject {
    protected BootProcessorMediator processorMediator;

    public void start(long startup) {
        for (BootStartConsumer resolver : this.processorMediator.getStarts()) {
            resolver.accept(startup);
        }
    }
}
