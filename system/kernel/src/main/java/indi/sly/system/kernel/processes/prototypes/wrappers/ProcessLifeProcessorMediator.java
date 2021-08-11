package indi.sly.system.kernel.processes.prototypes.wrappers;

import indi.sly.system.kernel.core.prototypes.wrappers.AMediator;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorCreateFunction;
import indi.sly.system.kernel.processes.lang.ProcessLifeProcessorEndFunction;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProcessLifeProcessorMediator extends AMediator {
    public ProcessLifeProcessorMediator() {
        this.creates = new ArrayList<>();
        this.ends = new ArrayList<>();
    }

    private final List<ProcessLifeProcessorCreateFunction> creates;
    private final List<ProcessLifeProcessorEndFunction> ends;

    public List<ProcessLifeProcessorCreateFunction> getCreates() {
        return this.creates;
    }

    public List<ProcessLifeProcessorEndFunction> getEnds() {
        return this.ends;
    }
}
