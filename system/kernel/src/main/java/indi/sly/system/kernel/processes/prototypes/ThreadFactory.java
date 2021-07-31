package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.StatusAlreadyFinishedException;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.core.prototypes.AFactory;
import indi.sly.system.kernel.processes.values.ThreadContextType;
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

    public ThreadObject create(UUID processID) {
        UserSpaceDefinition userSpace = this.factoryManager.getUserSpace();
        Stack<ThreadObject> threads = userSpace.getThreads();

        ThreadObject thread = this.factoryManager.create(ThreadObject.class);
        thread.setProcessID(processID);

        ThreadStatusObject threadStatus = thread.getStatus();
        threadStatus.initialize();

        ThreadContextObject context = thread.getContext();
        if (threads.isEmpty()) {
            context.setType(ThreadContextType.USER);
        } else {
            context.setType(ThreadContextType.DAEMON);
        }

        threads.push(thread);

        return thread;
    }

    public void end() {
        UserSpaceDefinition userSpace = this.factoryManager.getUserSpace();
        Stack<ThreadObject> threads = userSpace.getThreads();

        if (threads.isEmpty()) {
            throw new StatusAlreadyFinishedException();
        }

        ThreadObject thread = threads.peek();

        ThreadStatusObject threadStatus = thread.getStatus();
        threadStatus.end();

        threads.pop();
    }
}
