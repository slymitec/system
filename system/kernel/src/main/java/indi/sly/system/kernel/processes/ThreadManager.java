package indi.sly.system.kernel.processes;

import indi.sly.system.common.lang.StatusAlreadyExistedException;
import indi.sly.system.common.lang.StatusNotExistedException;
import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.kernel.processes.values.ThreadRunDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.UUID;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadManager extends AManager {
    public ThreadObject getCurrentThread() {
        UserSpaceDefinition userSpace = this.factoryManager.getUserSpace();
        ThreadObject thread = userSpace.getThread();

        if (ObjectUtil.isAnyNull(thread)) {
            throw new StatusNotExistedException();
        }

        return thread;
    }

    public ThreadObject createCurrentThread(UUID processID) {
        UserSpaceDefinition userSpace = this.factoryManager.getUserSpace();
        if (ObjectUtil.allNotNull(userSpace.getThread())) {
            throw new StatusAlreadyExistedException();
        }

        ThreadObject thread = this.factoryManager.create(ThreadObject.class);
        thread.setProcessID(processID);
        thread.start();

        userSpace.setThread(thread);

        return thread;
    }

    public void endCurrentThread() {
        ThreadObject thread = this.getCurrentThread();
        thread.end();

        UserSpaceDefinition userSpace = this.factoryManager.getUserSpace();
        userSpace.setThread(null);
    }

}
