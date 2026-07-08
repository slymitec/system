package indi.sly.system.kernel.core.enviroment.containers;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import jakarta.inject.Named;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Stack;

@Named
@RequestScope
public class UserSpace extends ASpace {
    public UserSpace() {
        this.threads = new ThreadLocal<>();
    }

    private final ThreadLocal<Stack<ThreadObject>> threads;
    private AUserExtensionSpaceDefinition serviceSpace;

    public Stack<ThreadObject> getThreads() {
        return this.threads.get();
    }

    public void setThreads(Stack<ThreadObject> threads) {
        if (ObjectUtil.isAnyNull(threads)) {
            throw new ConditionParametersException();
        }

        this.threads.set(threads);
    }

    public AUserExtensionSpaceDefinition getServiceSpace() {
        return this.serviceSpace;
    }

    public void setServiceSpace(AUserExtensionSpaceDefinition serviceSpace) {
        this.serviceSpace = serviceSpace;
    }
}
