package indi.sly.system.kernel.core.environment.containers;

import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.ArrayDeque;
import java.util.Deque;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserSpace extends ASystemSpace {
    public UserSpace() {
        this.threads = ThreadLocal.withInitial(ArrayDeque::new);
    }

    private final ThreadLocal<Deque<ThreadObject>> threads;

    public Deque<ThreadObject> getThreads() {
        return this.threads.get();
    }
}
