package indi.sly.system.kernel.processes.prototypes.wrappers;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.lang.CreateProcessFunction;
import indi.sly.system.kernel.processes.lang.EndProcessFunction;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessLifeCycleProcessorMediator extends APrototype {
    public ProcessLifeCycleProcessorMediator() {
        this.creates = new ArrayList<>();
        this.ends = new ArrayList<>();
    }

    private final List<CreateProcessFunction> creates;
    private final List<EndProcessFunction> ends;

    public List<CreateProcessFunction> getCreates() {
        return this.creates;
    }

    public List<EndProcessFunction> getEnds() {
        return this.ends;
    }
}
