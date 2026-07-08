package indi.sly.system.test;

import indi.sly.system.common.lang.StatusNotReadyException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.enviroment.configutations.KernelConfiguration;
import indi.sly.system.kernel.core.enviroment.containers.UserSpace;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.kernel.processes.prototypes.ThreadObject;
import indi.sly.system.services.faces.AController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public abstract class ATestController extends AController {
    public final void initall(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        UserSpace userSpace = (UserSpace) session.getAttribute("userSpace");

        if (ObjectUtil.isAnyNull(this.coreManager)) {
            synchronized (this) {
                if (ObjectUtil.isAnyNull(this.coreManager)) {
                    this.init();
                }
            }
        }

        if (ObjectUtil.isAnyNull(userSpace)) {
            throw new StatusNotReadyException();
        }

        this.coreManager.setUserSpace(userSpace);

        ThreadManager threadManager = this.coreManager.getManager(ThreadManager.class);
        ThreadObject thread = threadManager.create(new KernelConfiguration().PROCESSES_PROTOTYPE_SYSTEM_ID);
    }
}
