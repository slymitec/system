package indi.sly.system.kernel.core.enviroment.values;

import indi.sly.system.kernel.processes.prototypes.ThreadObject;

import java.util.Stack;

public class UserSpaceDefinition extends ASpaceDefinition<UserSpaceDefinition> {
    public UserSpaceDefinition() {
        this.threads = new Stack<>();
    }

    private final Stack<ThreadObject> threads;
    private AUserSpaceExtensionDefinition<?> serviceSpace;

    public Stack<ThreadObject> getThreads() {
        return this.threads;
    }

    public AUserSpaceExtensionDefinition<?> getServiceSpace() {
        return this.serviceSpace;
    }

    public void setServiceSpace(AUserSpaceExtensionDefinition<?> serviceSpace) {
        this.serviceSpace = serviceSpace;
    }
}
