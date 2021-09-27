package indi.sly.system.kernel.core.enviroment.values;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;

import java.util.Stack;

public class UserSpaceDefinition extends ASpaceDefinition<UserSpaceDefinition> {
    public UserSpaceDefinition() {
        this.threads = new ThreadLocal<>();
    }

    private final ThreadLocal<Stack<ThreadObject>> threads;
    private AUserSpaceExtensionDefinition<?> serviceSpace;

    public Stack<ThreadObject> getThreads() {
        return this.threads.get();
    }

    public void setThreads(Stack<ThreadObject> threads) {
        if (ObjectUtil.isAnyNull(threads)) {
            throw new ConditionParametersException();
        }

        this.threads.set(threads);
    }

    public AUserSpaceExtensionDefinition<?> getServiceSpace() {
        return this.serviceSpace;
    }

    public void setServiceSpace(AUserSpaceExtensionDefinition<?> serviceSpace) {
        this.serviceSpace = serviceSpace;
    }
}
