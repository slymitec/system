package indi.sly.system.kernel.core.environment.containers;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import jakarta.inject.Named;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Deque;
import java.util.Stack;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserSpace extends ASystemSpace {
    public UserSpace() {
        this.threads = new ThreadLocal<>();
    }

    private final ThreadLocal<Deque<ThreadObject>> threads;

    public Deque<ThreadObject> getThreads() {
        return this.threads.get();
    }

    public void setThreads(Deque<ThreadObject> threads) {
        if (ObjectUtil.isAnyNull(threads)) {
            throw new ConditionParametersException();
        }

        this.threads.set(threads);
    }
}
