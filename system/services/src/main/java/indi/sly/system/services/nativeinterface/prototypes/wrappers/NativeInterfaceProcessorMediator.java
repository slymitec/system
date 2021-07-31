package indi.sly.system.services.nativeinterface.prototypes.wrappers;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.nativeinterface.lang.FinishConsumer;
import indi.sly.system.services.nativeinterface.lang.RunConsumer;
import indi.sly.system.services.nativeinterface.lang.StartFunction;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NativeInterfaceProcessorMediator extends APrototype {
    public NativeInterfaceProcessorMediator() {
        this.starts = new ArrayList<>();
        this.finishes = new ArrayList<>();
        this.runs = new ArrayList<>();
    }

    private final List<StartFunction> starts;
    private final List<FinishConsumer> finishes;
    private final List<RunConsumer> runs;

    public List<StartFunction> getStarts() {
        return this.starts;
    }

    public List<FinishConsumer> getFinishes() {
        return this.finishes;
    }

    public List<RunConsumer> getRuns() {
        return this.runs;
    }
}
