package indi.sly.system.services.faces;

import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.SpringHelper;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.kernel.core.FactoryManager;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.core.values.HandleEntryDefinition;
import indi.sly.system.services.core.environment.values.ServiceUserSpaceExtensionDefinition;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import java.util.UUID;

@WebListener
public class WebHttpSessionListener extends APrototype implements HttpSessionListener {
    public WebHttpSessionListener() {
        KernelSpaceDefinition kernelSpace = SpringHelper.getInstance(KernelSpaceDefinition.class);

        HandleEntryDefinition factoryManagerHandleEntry = kernelSpace.getClassedHandles().getOrDefault(FactoryManager.class, null);
        if (ObjectUtil.isAnyNull(factoryManagerHandleEntry)) {
            return;
        }
        UUID factoryManagerID = factoryManagerHandleEntry.getID();
        if (ValueUtil.isAnyNullOrEmpty(factoryManagerID)) {
            return;
        }
        this.factoryManager = (FactoryManager) kernelSpace.getCoreObjects().getOrDefault(factoryManagerID, null);
        this.factoryManager.check();
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();

        UserSpaceDefinition userSpace = new UserSpaceDefinition();
        userSpace.setServiceSpace(new ServiceUserSpaceExtensionDefinition());

        KernelSpaceDefinition kernelSpace = this.factoryManager.getKernelSpace();
        KernelConfigurationDefinition kernelConfiguration = kernelSpace.getConfiguration();

        this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);

        session.setAttribute("userSpace", userSpace);
        session.setMaxInactiveInterval(60 * 60);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();

        session.removeAttribute("userSpace");
    }
}
