package indi.sly.system.services.job.prototypes;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.StringUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.prototypes.AIndependentValueProcessObject;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import indi.sly.system.services.job.lang.JobProcessorContentFunction;
import indi.sly.system.services.job.lang.JobProcessorFinishConsumer;
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
        this.lock(LockType.READ);
        this.init();

        UUID id = this.value.getID();

        this.lock(LockType.NONE);
        return id;
    }

    public long getRuntime() {
        return this.status.getRuntime();
    }

    public void start() {
        List<JobProcessorStartFunction> resolvers = this.processorMediator.getStarts();

        this.lock(LockType.READ);
        this.init();

        for (JobProcessorStartFunction resolver : resolvers) {
            resolver.accept(this.value, this.status);
        }

        this.lock(LockType.NONE);
    }

    public void finish() {
        List<JobProcessorFinishConsumer> resolvers = this.processorMediator.getFinishes();

        this.lock(LockType.READ);
        this.init();

        for (JobProcessorFinishConsumer resolver : resolvers) {
            resolver.accept(this.value, this.status);
        }

        this.lock(LockType.NONE);
    }

    public synchronized void run(String name) {
        if (StringUtil.isNameIllegal(name)) {
            throw new ConditionParametersException();
        }

        JobContentObject content = this.getContent();

        List<JobProcessorRunConsumer> resolvers = this.processorMediator.getRuns();

        this.lock(LockType.READ);
        this.init();

        for (JobProcessorRunConsumer resolver : resolvers) {
            resolver.accept(this.value, this.status, name, this::run, content);
        }

        this.lock(LockType.NONE);
    }

    public synchronized JobContentObject getContent() {
        ThreadContextObject threadContext = null;

        List<JobProcessorContentFunction> resolvers = this.processorMediator.getContents();

        this.lock(LockType.READ);
        this.init();

        for (JobProcessorContentFunction resolver : resolvers) {
            threadContext = resolver.apply(this.value, this.status, threadContext);
        }

        this.lock(LockType.NONE);

        JobContentObject jobContent = this.factoryManager.create(JobContentObject.class);
        jobContent.threadContext = threadContext;

        return jobContent;
    }
}
