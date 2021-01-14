package indi.sly.system.kernel.processes;

import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.AManager;
import indi.sly.system.kernel.core.enviroment.UserSpace;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ThreadManager extends AManager {
    public ThreadObject getCurrentThread() {
        UserSpace userSpace = this.factoryManager.getUserSpace();
        ThreadObject thread = userSpace.getThread();

        if (ObjectUtil.isAnyNull(thread)) {
            throw new StatusNotReadyException();
        }

        return thread;
    }

    public ThreadObject createCurrentThreads() {
        ThreadObject thread = this.factoryManager.create(ThreadObject.class);

        //


        UserSpace userSpace = this.factoryManager.getUserSpace();
        userSpace.setThread(thread);

        return thread;
    }

}
