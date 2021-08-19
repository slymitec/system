package indi.sly.system.services.job.prototypes.processors;

import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.services.job.lang.JobProcessorContentFunction;
import indi.sly.system.services.job.prototypes.wrappers.JobProcessorMediator;
import indi.sly.system.services.job.values.JobDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JobContentResolver extends AJobResolver {
    public JobContentResolver() {
        this.content = (job, status, threadRun) -> {
            ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
            ThreadObject thread = threadManager.getCurrent();

            return thread.getContext();
        };
    }

    @Override
    public int order() {
        return 3;
    }

    private final JobProcessorContentFunction content;

    @Override
    public void resolve(JobDefinition job, JobProcessorMediator processorMediator) {
        processorMediator.getContents().add(this.content);
    }
}
