package indi.sly.system.boot.test;

import indi.sly.system.common.ABase;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.FactoryManager;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.processes.ThreadManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class AController extends ABase {
    @Autowired
    private KernelSpaceDefinition kernelSpaceDefinition;

    @Autowired
    private BootController bootController;

    protected FactoryManager factoryManager;

    public final void init(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        if (ValueUtil.isAnyNullOrEmpty(bootController.getRet())) {
            this.bootController.Boot(request, response, session);
        }

        KernelConfigurationDefinition kernelConfiguration = this.kernelSpaceDefinition.getConfiguration();

        this.factoryManager = (FactoryManager) this.kernelSpaceDefinition.getCoreObjects().getOrDefault(
                this.kernelSpaceDefinition.getClassedHandles().getOrDefault(
                        FactoryManager.class, null).getID(), null);

        factoryManager.setUserSpace(new UserSpaceDefinition());

        ThreadManager threadManager = factoryManager.getManager(ThreadManager.class);
        threadManager.create(kernelConfiguration.PROCESSES_PROTOTYPE_SYSTEM_ID);
    }
}
