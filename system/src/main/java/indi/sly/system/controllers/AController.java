package indi.sly.system.controllers;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.kernel.core.FactoryManager;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.services.auxiliary.AuxiliaryService;
import indi.sly.system.services.auxiliary.prototypes.UserContextObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class AController extends APrototype {
    @Autowired
    private KernelSpaceDefinition kernelSpace;

    protected final UserContextObject init(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        KernelConfigurationDefinition kernelConfiguration = this.kernelSpace.getConfiguration();

        this.factoryManager = (FactoryManager) this.kernelSpace.getCoreObjects().getOrDefault(
                this.kernelSpace.getClassedHandles().getOrDefault(FactoryManager.class, null).getID(), null);

        UserContextObject userContext = null;

        if (ObjectUtil.allNotNull(this.factoryManager)) {
            AuxiliaryService auxiliaryService = this.factoryManager.getService(AuxiliaryService.class);

            userContext = auxiliaryService.create(request.getParameter("Data"));
        }

        return userContext;
    }
}
