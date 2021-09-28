package indi.sly.system.services.job.prototypes.wrappers;

import indi.sly.system.kernel.core.prototypes.wrappers.AMediator;
import indi.sly.system.services.job.lang.TaskProcessorContentFunction;
import indi.sly.system.services.job.lang.TaskProcessorFinishConsumer;
import indi.sly.system.services.job.lang.TaskProcessorRunConsumer;
import indi.sly.system.services.job.lang.TaskProcessorStartFunction;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskProcessorMediator extends AMediator {
    public TaskProcessorMediator() {
        this.starts = new ArrayList<>();
        this.finishes = new ArrayList<>();
        this.runs = new ArrayList<>();
        this.contents = new ArrayList<>();
    }

    private final List<TaskProcessorStartFunction> starts;
    private final List<TaskProcessorFinishConsumer> finishes;
    private final List<TaskProcessorRunConsumer> runs;
    private final List<TaskProcessorContentFunction> contents;

    public List<TaskProcessorStartFunction> getStarts() {
        return this.starts;
    }

    public List<TaskProcessorFinishConsumer> getFinishes() {
        return this.finishes;
    }

    public List<TaskProcessorRunConsumer> getRuns() {
        return this.runs;
    }

    public List<TaskProcessorContentFunction> getContents() {
        return this.contents;
    }
}
