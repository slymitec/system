package indi.sly.system.boot.test;

import indi.sly.system.common.ABase;
import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.UUIDUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.FactoryManager;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.processes.ThreadManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

public abstract class ATController extends ABase {
    @Autowired
    private KernelSpaceDefinition kernelSpaceDefinition;

    @Autowired
    private BootController bootController;

    protected FactoryManager factoryManager;
    protected KernelConfigurationDefinition kernelConfiguration;

    public final void init(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        UUID processID = null;

        String processIDText = request.getParameter("ProcessID");
        if (!ValueUtil.isAnyNullOrEmpty(processIDText)) {
            processID = UUIDUtil.getFromString(processIDText);

            if (ObjectUtil.isAnyNull(processID)) {
                throw new ConditionParametersException();
            }
        }

        this.init(request, response, session, processID);
    }

    public final void init(HttpServletRequest request, HttpServletResponse response, HttpSession session, UUID processID) {
        if (ValueUtil.isAnyNullOrEmpty(bootController.getRet())) {
            this.bootController.boot(request, response, session);
        }

        this.kernelConfiguration = this.kernelSpaceDefinition.getConfiguration();

        this.factoryManager = (FactoryManager) this.kernelSpaceDefinition.getCoreObjects().getOrDefault(
                this.kernelSpaceDefinition.getClassedHandles().getOrDefault(
                        FactoryManager.class, null).getID(), null);

        UserSpaceDefinition userSpace = new UserSpaceDefinition();
        this.factoryManager.setUserSpace(() -> userSpace);
        this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, 16L);

        ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);
        if (ValueUtil.isAnyNullOrEmpty(processID)) {
            //threadManager.create(this.kernelConfiguration.PROCESSES_PROTOTYPE_SYSTEM_ID);
        } else {
            threadManager.create(processID);
        }
    }
}
