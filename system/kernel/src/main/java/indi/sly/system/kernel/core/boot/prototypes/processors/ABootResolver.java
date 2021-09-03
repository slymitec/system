package indi.sly.system.kernel.core.boot.prototypes.processors;

import indi.sly.system.kernel.core.boot.prototypes.wrappers.BootProcessorMediator;
import indi.sly.system.kernel.core.prototypes.processors.AResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ABootResolver extends AResolver {
    public abstract void resolve(BootProcessorMediator processorMediator);
}
