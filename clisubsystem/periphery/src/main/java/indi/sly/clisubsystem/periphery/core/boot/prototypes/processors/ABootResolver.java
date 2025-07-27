package indi.sly.clisubsystem.periphery.core.boot.prototypes.processors;

import indi.sly.clisubsystem.periphery.core.boot.prototypes.wrappers.BootProcessorMediator;
import indi.sly.clisubsystem.periphery.core.prototypes.processors.AResolver;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ABootResolver extends AResolver {
    public abstract void resolve(BootProcessorMediator processorMediator);
}
