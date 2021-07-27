package indi.sly.system.kernel.processes.prototypes.wrappers;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.lang.CreateProcessFunction;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashSet;
import java.util.Set;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessCreatorProcessorMediator extends APrototype {
    public ProcessCreatorProcessorMediator() {
        this.creates = new HashSet<>();

    }

    private final Set<CreateProcessFunction> creates;

    public Set<CreateProcessFunction> getCreates() {
        return this.creates;
    }
}
