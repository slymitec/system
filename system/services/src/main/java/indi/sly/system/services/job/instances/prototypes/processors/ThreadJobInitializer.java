package indi.sly.system.services.job.instances.prototypes.processors;

import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.kernel.processes.prototypes.ThreadStatusObject;
import indi.sly.system.kernel.processes.values.ThreadContextType;
import indi.sly.system.services.job.lang.JobRunConsumer;
import indi.sly.system.services.job.prototypes.JobContentObject;
import indi.sly.system.services.job.prototypes.processors.AJobInitializer;
import indi.sly.system.services.job.values.JobDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadJobInitializer extends AJobInitializer {
    public ThreadJobInitializer() {
        this.register("createThread", this::createThread);
        this.register("endThread", this::endThread);
    }

    @Override
    public void start(JobDefinition job) {
    }

    @Override
    public void finish(JobDefinition job) {
    }

    private void createThread(JobRunConsumer run, JobContentObject content) {
        UUID parameter_ProcessID = content.getDatum(UUID.class, "Processes_Process_ID");

        //

        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

        //

        if (threadManager.size() != 0) {
            throw new StatusAlreadyExistedException();
        }

        ThreadObject thread = threadManager.create(parameter_ProcessID);

        ThreadStatusObject threadStatus = thread.getStatus();
        threadStatus.running();
    }

    private void endThread(JobRunConsumer run, JobContentObject content) {
        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

        //

        ThreadObject thread = threadManager.getCurrent();

        ThreadContextObject threadContext = thread.getContext();
        if (threadContext.getType() != ThreadContextType.USER) {
            throw new StatusRelationshipErrorException();
        }

        ThreadStatusObject threadStatus = thread.getStatus();
        threadStatus.die();
    }
}
