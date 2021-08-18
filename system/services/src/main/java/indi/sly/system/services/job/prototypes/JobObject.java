package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.*;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.kernel.core.prototypes.AIndependentValueProcessObject;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import indi.sly.system.services.job.JobService;
import indi.sly.system.services.job.lang.JobProcessorFinishConsumer;
import indi.sly.system.services.job.lang.JobProcessorContentFunction;
import indi.sly.system.services.job.lang.JobProcessorRunConsumer;
import indi.sly.system.services.job.lang.JobProcessorStartFunction;
import indi.sly.system.services.job.prototypes.wrappers.JobProcessorMediator;
import indi.sly.system.services.job.values.JobDefinition;
import indi.sly.system.services.job.values.JobStatusDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.List;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobObject extends AIndependentValueProcessObject<JobDefinition> {
    protected JobProcessorMediator processorMediator;
    protected JobStatusDefinition status;

    public UUID getID() {
        this.init();

        return this.value.getID();
    }

    public long getRuntime() {
        return this.status.getRuntime();
    }

    private synchronized JobDefinition getSelf() {
        this.init();

        return this.value;
    }

    public void start() {
        JobDefinition job = this.getSelf();

        List<JobProcessorStartFunction> resolvers = this.processorMediator.getStarts();

        for (JobProcessorStartFunction resolver : resolvers) {
            resolver.accept(job, this.status);
        }
    }

    public void finish() {
        JobDefinition job = this.getSelf();

        List<JobProcessorFinishConsumer> resolvers = this.processorMediator.getFinishes();

        for (JobProcessorFinishConsumer resolver : resolvers) {
            resolver.accept(job, this.status);
        }
    }

    public synchronized void run(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        JobDefinition job = this.getSelf();
        JobContentObject content = this.getContent();

        List<JobProcessorRunConsumer> resolvers = this.processorMediator.getRuns();

        for (JobProcessorRunConsumer resolver : resolvers) {
            resolver.accept(job, this.status, name, this::run, content);
        }
    }

    public synchronized JobContentObject getContent() {
        JobDefinition job = this.getSelf();

        JobService jobService = this.factoryManager.getService(JobService.class);

        ThreadContextObject threadContext = null;

        List<JobProcessorContentFunction> resolvers = this.processorMediator.getContents();

        for (JobProcessorContentFunction resolver : resolvers) {
            threadContext = resolver.apply(job, this.status, threadContext);
        }

        JobContentObject jobContent = this.factoryManager.create(JobContentObject.class);
        jobContent.threadContext = threadContext;

        return jobContent;
    }
}
