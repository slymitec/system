package indi.sly.system.services.job.prototypes.wrappers;

import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.job.lang.JobProcessorFinishConsumer;
import indi.sly.system.services.job.lang.JobProcessorContentFunction;
import indi.sly.system.services.job.lang.JobProcessorRunConsumer;
import indi.sly.system.services.job.lang.JobProcessorStartFunction;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobProcessorMediator extends APrototype {
    public JobProcessorMediator() {
        this.starts = new ArrayList<>();
        this.finishes = new ArrayList<>();
        this.runs = new ArrayList<>();
        this.contents = new ArrayList<>();
    }

    private final List<JobProcessorStartFunction> starts;
    private final List<JobProcessorFinishConsumer> finishes;
    private final List<JobProcessorRunConsumer> runs;
    private final List<JobProcessorContentFunction> contents;

    public List<JobProcessorStartFunction> getStarts() {
        return this.starts;
    }

    public List<JobProcessorFinishConsumer> getFinishes() {
        return this.finishes;
    }

    public List<JobProcessorRunConsumer> getRuns() {
        return this.runs;
    }

    public List<JobProcessorContentFunction> getContents() {
        return this.contents;
    }
}
