package indi.sly.system.services.center.prototypes.wrappers;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.center.lang.CenterProcessorFinishConsumer;
import indi.sly.system.services.center.lang.CenterProcessorContentFunction;
import indi.sly.system.services.center.lang.CenterProcessorRunConsumer;
import indi.sly.system.services.center.lang.CenterProcessorStartFunction;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CenterProcessorMediator extends APrototype {
    public CenterProcessorMediator() {
        this.starts = new ArrayList<>();
        this.finishes = new ArrayList<>();
        this.runs = new ArrayList<>();
        this.contents = new ArrayList<>();
    }

    private final List<CenterProcessorStartFunction> starts;
    private final List<CenterProcessorFinishConsumer> finishes;
    private final List<CenterProcessorRunConsumer> runs;
    private final List<CenterProcessorContentFunction> contents;

    public List<CenterProcessorStartFunction> getStarts() {
        return this.starts;
    }

    public List<CenterProcessorFinishConsumer> getFinishes() {
        return this.finishes;
    }

    public List<CenterProcessorRunConsumer> getRuns() {
        return this.runs;
    }

    public List<CenterProcessorContentFunction> getContents() {
        return this.contents;
    }
}
