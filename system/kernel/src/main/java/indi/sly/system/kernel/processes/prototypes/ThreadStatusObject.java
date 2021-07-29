package indi.sly.system.kernel.processes.prototypes;

import indi.sly.system.common.lang.ISerializeCapable;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.values.LockType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.core.prototypes.AValueProcessPrototype;
import indi.sly.system.kernel.processes.values.ThreadDefinition;
import indi.sly.system.kernel.processes.values.ThreadStatusType;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Stack;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadStatusObject extends AValueProcessPrototype<ThreadDefinition> {
    public long get() {
        this.init();

        return this.value.getStatus();
    }

    public void initialize() {
        if (this.value.getStatus() != ThreadStatusType.NULL) {
            throw new StatusNotReadyException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setStatus(ThreadStatusType.INITIALIZATION);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public void start(ISerializeCapable<?>[] arguments) {
        if (this.value.getStatus() != ThreadStatusType.INITIALIZATION) {
            throw new StatusNotReadyException();
        }

        this.lock(LockType.WRITE);
        this.init();

        if (ObjectUtil.notNull(arguments)) {
            this.value.getContext().getRun().setArguments(arguments);
        }

        this.value.setStatus(ThreadStatusType.RUNNING);

        this.fresh();
        this.lock(LockType.NONE);
    }

    public ISerializeCapable<?>[] end() {
        if (this.value.getStatus() != ThreadStatusType.RUNNING) {
            throw new StatusNotReadyException();
        }

        this.init();

        UserSpaceDefinition userSpace = this.factoryManager.getUserSpace();
        Stack<ThreadObject> threads = userSpace.getThreads();

        if (threads.isEmpty()) {
            throw new StatusNotExistedException();
        }
        if (!threads.peek().getID().equals(this.value.getID())) {
            throw new StatusNotReadyException();
        }

        this.lock(LockType.WRITE);
        this.init();

        this.value.setStatus(ThreadStatusType.DIED);

        threads.pop();

        ISerializeCapable<?>[] results = this.value.getContext().getRun().getResults();

        this.fresh();
        this.lock(LockType.NONE);

        return results;
    }
}
