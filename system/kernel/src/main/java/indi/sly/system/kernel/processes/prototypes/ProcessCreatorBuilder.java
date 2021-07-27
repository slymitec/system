package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.prototypes.wrappers.ProcessCreatorProcessorMediator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreatorBuilder extends APrototype {
    protected ProcessFactory factory;
    protected ProcessCreatorProcessorMediator processorMediator;

    public ProcessObject build() {
        return null;
    }
}
