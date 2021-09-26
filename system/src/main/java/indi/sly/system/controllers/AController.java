package indi.sly.system.controllers;

import indi.sly.system.common.lang.AKernelException;
import indi.sly.system.common.lang.Provider;
import indi.sly.system.common.supports.ObjectUtil;
import indi.sly.system.common.supports.ValueUtil;
import indi.sly.system.controllers.values.UserContentDefinition;
import indi.sly.system.controllers.values.UserContentRequestDefinition;
import indi.sly.system.controllers.values.UserContentResponseDefinition;
import indi.sly.system.kernel.core.FactoryManager;
import indi.sly.system.kernel.core.enviroment.values.KernelConfigurationDefinition;
import indi.sly.system.kernel.core.enviroment.values.KernelSpaceDefinition;
import indi.sly.system.kernel.core.enviroment.values.SpaceType;
import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import indi.sly.system.kernel.core.prototypes.APrototype;
import indi.sly.system.kernel.processes.ThreadManager;
import indi.sly.system.services.core.prototypes.TransactionalActionObject;
import indi.sly.system.services.core.values.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

public abstract class AController extends APrototype {
    @Autowired
    private KernelSpaceDefinition kernelSpace;

    protected final UserContentDefinition init(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        KernelConfigurationDefinition kernelConfiguration = this.kernelSpace.getConfiguration();

        UserContentDefinition userContent = new UserContentDefinition();
        UserContentRequestDefinition userContentRequest = userContent.getRequest();
        UserContentResponseDefinition userContentResponse = userContent.getResponse();

        try {
            UUID processID = ObjectUtil.transferFromString(UUID.class, request.getParameter("processID"));
            userContentRequest.setProcessID(processID);

            //...
        } catch (AKernelException ignored) {

        }

        this.factoryManager = (FactoryManager) this.kernelSpace.getCoreObjects().getOrDefault(
                this.kernelSpace.getClassedHandles().getOrDefault(FactoryManager.class, null).getID(), null);

        if (ObjectUtil.allNotNull(this.factoryManager)) {
            this.factoryManager.setUserSpace(new UserSpaceDefinition());
            this.factoryManager.getCoreObjectRepository().setLimit(SpaceType.USER, kernelConfiguration.CORE_ENVIRONMENT_USER_SPACE_CORE_OBJECT_LIMIT);

            UUID processID = userContentRequest.getProcessID();

            if (!ValueUtil.isAnyNullOrEmpty(processID)) {
                ThreadManager threadManager = this.factoryManager.getManager(ThreadManager.class);

                TransactionalActionObject transactionalAction = this.factoryManager.create(TransactionalActionObject.class);

                transactionalAction.run(TransactionType.INDEPENDENCE, (Provider<Void>) () -> {
                    threadManager.create(processID);

                    return null;
                });
            }
        }

        return userContent;
    }
}
