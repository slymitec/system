package indi.sly.system.kernel.processes;

import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.processes.prototypes.ThreadContextObject;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.kernel.processes.prototypes.ThreadStatusObject;
import indi.sly.system.kernel.processes.values.ThreadContextType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Stack;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadManager extends AManager {
    public ThreadObject getCurrent() {
        UserSpaceDefinition userSpace = this.factoryManager.getUserSpace();
        Stack<ThreadObject> threads = userSpace.getThreads();

        if (threads.isEmpty()) {
            throw new StatusNotExistedException();
        }

        return threads.peek();
    }

    public int size() {
        UserSpaceDefinition userSpace = this.factoryManager.getUserSpace();
        Stack<ThreadObject> threads = userSpace.getThreads();

        return threads.size();
    }

    public ThreadObject create(UUID processID) {
        UserSpaceDefinition userSpace = this.factoryManager.getUserSpace();
        Stack<ThreadObject> threads = userSpace.getThreads();

        ThreadObject thread = this.factoryManager.create(ThreadObject.class);
        thread.setProcessID(processID);

        ThreadStatusObject threadStatus = thread.getStatus();
        threadStatus.initialize();

        ThreadContextObject context = thread.getContext();
        if (threads.isEmpty()) {
            context.setType(ThreadContextType.CURRENT);
        } else {
            context.setType(ThreadContextType.SHADOW);
        }

        userSpace.getThreads().push(thread);

        return thread;
    }

    public void end() {
        UserSpaceDefinition userSpace = this.factoryManager.getUserSpace();
        Stack<ThreadObject> threads = userSpace.getThreads();

        while (!threads.isEmpty()) {
            ThreadObject thread = threads.peek();

            ThreadStatusObject threadStatus = thread.getStatus();
            threadStatus.end();
        }
    }
}
