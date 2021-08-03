package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.StatusAlreadyFinishedException;
import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.processes.values.ThreadContextType;
import indi.sly.system.kernel.processes.values.ThreadStatusType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Stack;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadFactory extends AFactory {
    public void init() {
    }

    public ThreadObject buildThread(UUID processID) {
        ThreadObject thread = this.factoryManager.create(ThreadObject.class);

        thread.setProcessID(processID);

        return thread;
    }

    public ThreadBuilder createThread() {
        ThreadBuilder threadBuilder = this.factoryManager.create(ThreadBuilder.class);

        threadBuilder.factory = this;

        return threadBuilder;
    }

}