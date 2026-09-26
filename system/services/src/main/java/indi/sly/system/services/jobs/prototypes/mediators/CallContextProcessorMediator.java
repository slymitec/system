package indi.sly.system.services.jobs.prototypes.mediators;

import indi.sly.system.kernel.core.prototypes.AMediator;
import indi.sly.system.services.jobs.lang.CallContextProcessorCreateFunction;
import indi.sly.system.services.jobs.lang.CallContextProcessorEndFunction;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CallContextProcessorMediator extends AMediator {

    public CallContextProcessorMediator() {
        this.creates = new ArrayList<>();
        this.ends = new ArrayList<>();
    }

    private final List<CallContextProcessorCreateFunction> creates;
    private final List<CallContextProcessorEndFunction> ends;

    public List<CallContextProcessorCreateFunction> getCreates() {
        return this.creates;
    }

    public List<CallContextProcessorEndFunction> getEnds() {
        return this.ends;
    }
}
