package indi.sly.system.services.center.instances.prototypes.processors;

import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusRelationshipErrorException;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.kernel.processes.prototypes.ThreadStatusObject;
import indi.sly.system.kernel.processes.values.ThreadContextType;
import indi.sly.system.services.center.lang.InitializerConsumer;
import indi.sly.system.services.center.lang.CenterObjectRunConsumer;
import indi.sly.system.services.center.prototypes.CenterContentObject;
import indi.sly.system.services.center.prototypes.processors.ACenterInitializer;
import indi.sly.system.services.center.values.CenterDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LifeCenterInitializer extends ACenterInitializer {
    public LifeCenterInitializer() {
        Map<String, InitializerConsumer> runMethods = this.getRunMethods();

        runMethods.put("createThread", this::createThread);
        runMethods.put("endThread", this::endThread);
    }

    @Override
    public void start(CenterDefinition center) {
    }

    @Override
    public void finish(CenterDefinition center) {
    }

    private void createThread(CenterObjectRunConsumer run, CenterContentObject content) {
        UUID processID = content.getDatum(UUID.class, "processID");

        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

        if (threadManager.size() != 0) {
            throw new StatusAlreadyExistedException();
        }

        ThreadObject thread = threadManager.create(processID);

        ThreadStatusObject threadStatus = thread.getStatus();
        threadStatus.start();
    }

    private void endThread(CenterObjectRunConsumer run, CenterContentObject content) {
        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

        ThreadObject thread = threadManager.getCurrent();

        ThreadContextObject threadContext = thread.getContext();
        if (threadContext.getType() != ThreadContextType.USER) {
            throw new StatusRelationshipErrorException();
        }

        ThreadStatusObject threadStatus = thread.getStatus();
        threadStatus.end();
    }
}
