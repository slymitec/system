package indi.sly.system.kernel.core.enviroment.values;

import indi.sly.system.common.values.ADefinition;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Stack;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserSpaceDefinition extends ADefinition<UserSpaceDefinition> {
    public UserSpaceDefinition() {
        this.infoLock = new ReentrantReadWriteLock();
        this.threads = new Stack<>();
    }

    private final ReadWriteLock infoLock;

    public ReadWriteLock getInfoLock() {
        return this.infoLock;
    }

    private final Stack<ThreadObject> threads;

    public Stack<ThreadObject> getThreads() {
        return this.threads;
    }
}
