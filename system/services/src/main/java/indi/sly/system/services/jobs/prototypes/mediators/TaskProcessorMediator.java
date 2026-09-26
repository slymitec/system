package indi.sly.system.services.jobs.prototypes.mediators;

import indi.sly.system.kernel.core.prototypes.AMediator;
import indi.sly.system.services.jobs.lang.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import jakarta.inject.Named;

import java.util.ArrayList;
import java.util.List;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskProcessorMediator extends AMediator {
    public TaskProcessorMediator() {
        this.starts = new ArrayList<>();
        this.ends = new ArrayList<>();
        this.runs = new ArrayList<>();
        this.finishes = new ArrayList<>();
        this.contents = new ArrayList<>();
    }

    private final List<TaskProcessorStartConsumer> starts;
    private final List<TaskProcessorEndConsumer> ends;
    private final List<TaskProcessorRunConsumer> runs;
    private final List<TaskProcessorFinishConsumer> finishes;
    private final List<TaskProcessorContentFunction> contents;

    public List<TaskProcessorStartConsumer> getStarts() {
        return this.starts;
    }

    public List<TaskProcessorEndConsumer> getEnds() {
        return this.ends;
    }

    public List<TaskProcessorRunConsumer> getRuns() {
        return this.runs;
    }

    public List<TaskProcessorFinishConsumer> getFinishes() {
        return this.finishes;
    }

    public List<TaskProcessorContentFunction> getContents() {
        return this.contents;
    }
}
