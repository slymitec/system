package indi.sly.system.services.center.instances.prototypes.processors;

import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.kernel.processes.prototypes.ThreadStatusObject;
import indi.sly.system.kernel.processes.values.ThreadContextType;
import indi.sly.system.services.center.lang.CenterRunConsumer;
import indi.sly.system.services.center.prototypes.CenterContentObject;
import indi.sly.system.services.center.prototypes.processors.ACenterInitializer;
import indi.sly.system.services.center.values.CenterDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadCenterInitializer extends ACenterInitializer {
    public ThreadCenterInitializer() {
        this.register("createThread", this::createThread);
        this.register("endThread", this::endThread);
    }

    @Override
    public void start(CenterDefinition center) {
    }

    @Override
    public void finish(CenterDefinition center) {
    }

    private void createThread(CenterRunConsumer run, CenterContentObject content) {
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

    private void endThread(CenterRunConsumer run, CenterContentObject content) {
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
