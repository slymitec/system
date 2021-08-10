package indi.sly.system.kernel.core.enviroment.values;

import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.Stack;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserSpaceDefinition extends ASpaceDefinition<UserSpaceDefinition> {
    public UserSpaceDefinition() {
        this.threads = new Stack<>();
    }

    private final Stack<ThreadObject> threads;

    public Stack<ThreadObject> getThreads() {
        return this.threads;
    }
}
